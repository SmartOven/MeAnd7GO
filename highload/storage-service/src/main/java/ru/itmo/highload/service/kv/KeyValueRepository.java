package ru.itmo.highload.service.kv;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.itmo.highload.service.kv.util.Pair;
import ru.itmo.highload.service.kv.util.SortedPairList;
import ru.itmo.highload.service.kv.util.SparseIndexUtil;
import ru.itmo.highload.service.kv.util.SstableUtil;

@Service
public class KeyValueRepository {
    private static final Logger log = LogManager.getLogger();
    private static final long MIB_TO_BYTES = 1048576L;
    private static final long KIB_TO_BYTES = 1024L;
    private final MemTableWal memTableWal;
    private MemTable memTable;
    private final SortedPairList<String, SparseIndex> sparseIndexes;
    private final long memTableSizeBytes;
    private final long segmentSizeBytes;
    private final long mergedSizeBytes;
    private final Path ssTablesDirPath;
    private final Path indexDirPath;

    public KeyValueRepository(
            @Value("${mem-table.size.mib:10}") int memTableSizeMib,
            @Value("${storage.path:~/storage}") String storagePathString,
            @Value("${ss-table.segment.size.kib:4}") int segmentSizeKib,
            @Value("${ss-table.merged.size.mib:1}") int mergedSizeMib) {
        Path storagePath = Path.of(storagePathString);
        this.ssTablesDirPath = storagePath.resolve("ss-tables");
        this.indexDirPath = storagePath.resolve("sparse-indexes");
        this.memTableWal = new MemTableWal(storagePath.resolve("mem-table-wal"));
        this.memTable = memTableWal.loadFromFile();
        this.sparseIndexes = SparseIndexUtil.loadSparseIndexes(indexDirPath);
        this.memTableSizeBytes = memTableSizeMib * MIB_TO_BYTES;
        this.segmentSizeBytes = segmentSizeKib * KIB_TO_BYTES;
        this.mergedSizeBytes = mergedSizeMib * MIB_TO_BYTES;
        if (!ssTablesDirPath.toFile().exists()){
            var ignored = ssTablesDirPath.toFile().mkdirs();
        }
        if (!indexDirPath.toFile().exists()){
            var ignored = indexDirPath.toFile().mkdirs();
        }
    }

    public double getMemUsage() {
        return ((double) memTable.getMemSize()) / memTableSizeBytes * 100;
    }

    public String get(String key) {
        String value = memTable.get(key);
        if (value != null) {
            return value;
        }
        return findValueOnDisk(key);
    }

    public void set(String key, String value) {
        if (memTable.getMemSize() >= memTableSizeBytes) {
            dumpMemTableToSsTable();
            memTableWal.clearWal();
        }
        memTableWal.writeLine(new Pair<>(key, value));
        memTable.put(key, value);
    }

    /**
     * Мерджит SS-таблицы на диске, также мерджит и их SparseIndex-ы
     */
    @Scheduled(
            fixedDelayString = "${ss-table.compaction.timeout.millis:60000}",
            initialDelayString = "${ss-table.compaction.timeout.millis:60000}"
    )
    public void scheduleFixedDelayTask() {
        log.info("Starting merge and compress operations on SS-tables..");

        // Собираем список SS-таблиц для слияния
        List<Pair<String, SparseIndex>> dumpingSparseIndexes = new ArrayList<>(sparseIndexes);
        if (dumpingSparseIndexes.size() < 2) {
            log.info("Nothing to merge");
            return;
        }

        // Собираем все в больших Шлёпп
        List<MemTable> bigFloppaList = new ArrayList<>();
        MemTable bigFloppa = new MemTable();
        for (Pair<String, SparseIndex> sparseIndexPair : dumpingSparseIndexes) {
            String fileName = sparseIndexPair.getKey();
            SparseIndex sparseIndex = sparseIndexPair.getValue();
            for (Pair<String, Long> pair : sparseIndex) {
                MemTable tmpMemTable = SstableUtil.readMemTable(ssTablesDirPath.resolve(fileName), pair.getValue());
                if (tmpMemTable == null) {
                    continue;
                }
                bigFloppa.putAllIfAbsent(tmpMemTable);
                if (bigFloppa.getMemSize() >= mergedSizeBytes) {
                    bigFloppaList.add(bigFloppa);
                    bigFloppa = new MemTable();
                }
            }
        }
        if (!bigFloppa.isEmpty()) {
            bigFloppaList.add(bigFloppa);
        }

        // Разделяем их на сегменты и складываем в файлы
        List<Pair<String, SparseIndex>> floppaSparseIndexes = new ArrayList<>(bigFloppaList.size());
        long timestamp = System.currentTimeMillis();
        for (MemTable floppa : bigFloppaList) {
            SparseIndex floppaSparseIndex = new SparseIndex();
            String floppaTimestamp = String.valueOf(timestamp++);

            while (!floppa.isEmpty()) {
                MemTable segmentMemTable = new MemTable();
                while (segmentMemTable.getMemSize() < segmentSizeBytes && !floppa.isEmpty()) {
                    String key = floppa.firstKey();
                    String value = floppa.get(key);
                    segmentMemTable.put(key, value);
                    floppa.remove(key);
                }
                long offset = SstableUtil.dumpMemTable(segmentMemTable, ssTablesDirPath.resolve(floppaTimestamp));
                floppaSparseIndex.setIndex(segmentMemTable, offset);
            }
            log.info("Merged in file with name={}", floppaTimestamp);

            floppaSparseIndexes.add(new Pair<>(floppaTimestamp, floppaSparseIndex));
        }

        // Обновляем список индексов
        for (Pair<String, SparseIndex> floppaSparseIndex : floppaSparseIndexes) {
            sparseIndexes.insertSorted(floppaSparseIndex);
            SparseIndexUtil.createDump(floppaSparseIndex.getValue(), indexDirPath.resolve(floppaSparseIndex.getKey()));
        }
        for (Pair<String, SparseIndex> dumpingSparseIndex : dumpingSparseIndexes) {
            sparseIndexes.remove(dumpingSparseIndex);
            SparseIndexUtil.deleteDump(indexDirPath.resolve(dumpingSparseIndex.getKey()));
        }
        for (Pair<String, SparseIndex> dumpingSparseIndex : dumpingSparseIndexes) {
            String fileName = dumpingSparseIndex.getKey();
            var ignored = ssTablesDirPath.resolve(fileName).toFile().delete();
        }

        log.info("Merge and compress operations on SS-tables finished");
    }

    /**
     * Создает SparseIndex для MemTable и дампит их обоих на диск
     */
    private void dumpMemTableToSsTable() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        SparseIndex sparseIndex = new SparseIndex();

        long offset = SstableUtil.dumpMemTable(memTable, ssTablesDirPath.resolve(timestamp));
        sparseIndex.setIndex(memTable, offset);
        log.info("Memtable dumped on disk with name={}", timestamp);

        SparseIndexUtil.createDump(sparseIndex, indexDirPath.resolve(timestamp));
        sparseIndexes.insertSorted(new Pair<>(timestamp, sparseIndex));
        log.info("Sparse index for memtable dumped on disk with name={}", timestamp);

        memTable = new MemTable();
    }

    /**
     * Ищет значение по key на всех SS-таблицах на диске, начиная с самой свежей
     */
    private String findValueOnDisk(String key) {
        if (sparseIndexes.isEmpty()) {
            return null;
        }

        for (Pair<String, SparseIndex> sparseIndexPair : sparseIndexes) {
            String fileName = sparseIndexPair.getKey();
            SparseIndex sparseIndex = sparseIndexPair.getValue();
            var valueOptional = SstableUtil.findValueInSegment(ssTablesDirPath.resolve(fileName), sparseIndex, key);
            if (valueOptional.isPresent()) {
                return valueOptional.get();
            }
        }
        return null;
    }
}
