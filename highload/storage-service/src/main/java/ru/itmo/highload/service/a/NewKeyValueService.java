package ru.itmo.highload.service.a;

import java.util.Comparator;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class NewKeyValueService {
    private static final Logger log = LogManager.getLogger();
    private static final long BYTES_IN_MIB = 1048576L;
    private final MemTable memTable;
    private final SortedPairList<String, SparseIndex> sparseIndexes;
    private final long memTableSizeBytes;
    private final String storagePath;
    private volatile boolean isBusy;

    public NewKeyValueService(
            @Value("${mem-table.size.mib:10}") int memTableSizeMib,
            @Value("${ss-table.storage.path:~/storage}") String storagePath) {
        this.memTable = new MemTable();
        this.sparseIndexes = new SortedPairList<>(Comparator.comparing(Pair::getKey));
        this.memTableSizeBytes = memTableSizeMib * BYTES_IN_MIB;
        this.storagePath = storagePath;
        this.isBusy = false;
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

    // TODO
    //  По крону запускать слияние и уплотнение ss-таблиц на диске
    //  Уплотнять нужно SS-таблицы по сегментам, при этом обновлять разреженный индекс
    @Scheduled(
            fixedDelayString = "${ss-table.compaction.timeout.millis:60000}",
            initialDelayString = "${ss-table.compaction.timeout.millis:60000}"
    )
    public void scheduleFixedDelayTask() throws InterruptedException {
        log.info("Starting merge and compress operations on SS-tables..");
        this.isBusy = true;
        Thread.sleep(10000); // FIXME Busy waiting
        this.isBusy = false;
        log.info("Merge and compress operations on SS-tables finished");
    }

    public void dumpMemTable() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        SparseIndex sparseIndex = SstableUtil.dumpMemTable(memTable, storagePath + "/" + timestamp);
        sparseIndexes.insertSorted(new Pair<>(timestamp, sparseIndex));
        log.info("Memtable dumped on disk with name={}", timestamp);
    }

    private String findValueOnDisk(String key) {
        while (isBusy) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                log.error("Sleeping thread was interrupted", e);
                return null;
            }
        }

        if (sparseIndexes.isEmpty()) {
            return null;
        }

        for (Pair<String, SparseIndex> sparseIndexPair : sparseIndexes) {
            String fileName = sparseIndexPair.getKey();
            SparseIndex sparseIndex = sparseIndexPair.getValue();
            Pair<String, Integer> indexPair = sparseIndex.getNearestIndexPair(key);
            int offsetBytes = indexPair.getValue();
            Optional<String> valueOptional = SstableUtil.findValueInSegment(
                    storagePath + "/" + fileName,
                    offsetBytes,
                    key
            );
            if (valueOptional.isPresent()) {
                return valueOptional.get();
            }
        }
        return null;
    }
}
