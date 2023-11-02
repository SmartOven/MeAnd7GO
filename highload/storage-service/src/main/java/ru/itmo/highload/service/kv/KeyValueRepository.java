package ru.itmo.highload.service.kv;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class KeyValueRepository {
    private static final Logger log = LogManager.getLogger();
    private static final long MIB_TO_BYTES = 1048576L;
    private static final long KIB_TO_BYTES = 1024L;
    private MemTable memTable;
    private final SortedPairList<String, SparseIndex> sparseIndexes;
    private final long memTableSizeBytes;
    private final long segmentSizeBytes;
    private final long mergedSizeBytes;
    private final String storagePath;

    public KeyValueRepository(
            @Value("${mem-table.size.mib:10}") int memTableSizeMib,
            @Value("${ss-table.storage.path:~/storage}") String storagePath,
            @Value("${ss-table.segment.size.kib:4}") int segmentSizeKib,
            @Value("${ss-table.merged.size.mib:1}") int mergedSizeMib) {
        this.memTable = new MemTable();
        this.sparseIndexes = new SortedPairList<>(Comparator.comparing(Pair::getKey));
        this.memTableSizeBytes = memTableSizeMib * MIB_TO_BYTES;
        this.segmentSizeBytes = segmentSizeKib * KIB_TO_BYTES;
        this.mergedSizeBytes = mergedSizeMib * MIB_TO_BYTES;
        this.storagePath = storagePath.charAt(storagePath.length() - 1) == '/' ? storagePath : storagePath + '/';

        deleteOldFiles(storagePath);
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
            dumpMemTable();
        }
        memTable.set(key, value);
    }

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
        List<MemTable> bigFloppas = new ArrayList<>();
        MemTable bigFloppa = new MemTable();
        for (Pair<String, SparseIndex> sparseIndexPair : dumpingSparseIndexes) {
            String fileName = sparseIndexPair.getKey();
            SparseIndex sparseIndex = sparseIndexPair.getValue();
            for (Pair<String, Long> pair : sparseIndex) {
                MemTable tmpMemTable = SstableUtil.readMemTable(storagePath + fileName, pair.getValue());
                if (tmpMemTable == null) {
                    continue;
                }
                bigFloppa.putAllIfAbsent(tmpMemTable);
                if (bigFloppa.getMemSize() >= mergedSizeBytes) {
                    bigFloppas.add(bigFloppa);
                    bigFloppa = new MemTable();
                }
            }
        }
        if (!bigFloppa.isEmpty()) {
            bigFloppas.add(bigFloppa);
        }

        // Разделяем их на сегменты и складываем в файлы
        List<Pair<String, SparseIndex>> floppaSpareIndexes = new ArrayList<>(bigFloppas.size());
        long timestamp = System.currentTimeMillis();
        for (MemTable floppa : bigFloppas) {
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
                SstableUtil.dumpMemTable(
                        segmentMemTable,
                        storagePath + floppaTimestamp,
                        floppaSparseIndex
                );
            }
            log.info("Merged in file with name={}", floppaTimestamp);

            floppaSpareIndexes.add(new Pair<>(floppaTimestamp, floppaSparseIndex));
        }

        // Обновляем список индексов
        for (Pair<String, SparseIndex> floppaSpareIndex : floppaSpareIndexes) {
            sparseIndexes.insertSorted(floppaSpareIndex);
        }
        for (Pair<String, SparseIndex> dumpingSparseIndex : dumpingSparseIndexes) {
            sparseIndexes.remove(dumpingSparseIndex);
        }
        for (Pair<String, SparseIndex> dumpingSparseIndex : dumpingSparseIndexes) {
            String fileName = dumpingSparseIndex.getKey();
            File file = new File(storagePath + fileName);
            var ignored = file.delete();
        }

        log.info("Merge and compress operations on SS-tables finished");
    }

    private void dumpMemTable() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        SparseIndex sparseIndex = new SparseIndex();
        SstableUtil.dumpMemTable(memTable, storagePath + timestamp, sparseIndex);
        sparseIndexes.insertSorted(new Pair<>(timestamp, sparseIndex));
        log.info("Memtable dumped on disk with name={}", timestamp);
        memTable = new MemTable();
    }

    private String findValueOnDisk(String key) {

        if (sparseIndexes.isEmpty()) {
            return null;
        }

        for (Pair<String, SparseIndex> sparseIndexPair : sparseIndexes) {
            String fileName = sparseIndexPair.getKey();
            SparseIndex sparseIndex = sparseIndexPair.getValue();
            Optional<String> valueOptional = SstableUtil.findValueInSegment(
                    storagePath + fileName,
                    sparseIndex,
                    key
            );
            if (valueOptional.isPresent()) {
                return valueOptional.get();
            }
        }
        return null;
    }

    private static void deleteOldFiles(String storagePath) {
        File[] files = new File(storagePath).listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            var ignored = file.delete();
        }
    }
}
