package ru.itmo.highload.service.a;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * SS-таблицы работают так
 * При дампе memtable в SS-таблицу она сохраняется как есть с названием равным timestamp
 * При слиянии берем все дампы, создаем новый файл (дамп) и складываем в него,
 * опираясь на весь разреженный индекс целиком, самые новые значения по ключам,
 * а затем удаляем все дампы которые были использованы при слиянии
 */
@Service
public class NewKeyValueService {
    private static final Logger log = LogManager.getLogger();
    private static final long BYTES_IN_MIB = 1048576L;
    private final MemTable memTable;
    private final SparseIndex sparseIndex;
    private final long memTableSizeBytes;
    private final String storagePath;
    private volatile boolean isBusy;

    public NewKeyValueService(
            @Value("${mem-table.size.mib:10}") int memTableSizeMib,
            @Value("${ss-table.storage.path:~/storage}") String storagePath) {
        this.memTable = new MemTable();
        this.sparseIndex = new SparseIndex();
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
    //  (так как в таком случае все offset съедут)
    //  Тут также надо не забыть что изменять sparse index нужно в копии, а затем подменить линки
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

    // TODO
    //  При дампе memtable надо не забыть добавить индексы сегментов в sparse index
    public void dumpMemTable() {
        long timestamp = System.currentTimeMillis();
        log.info("Memtable dumped on disk with name={}", timestamp);
        // FIXME Ничего не происходит
    }

    // TODO
    //  При поиске по SS-таблицам смотрим на разреженный индекс, по нему находим нужную
    //  SS-таблицу (и нужный сегмент), этот сегмент кладем в оперативку,
    //  разжимаем и добываем нужное значение
    private String findValueOnDisk(String key) {
        while (isBusy) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                log.error("Sleeping thread was interrupted", e);
                return null;
            }
        }

        File storageDir = new File(storagePath);
        File[] storageFiles = storageDir.listFiles();
        if (storageFiles == null) {
            return null;
        }

        for (File sstable : storageFiles) {

        }

        long timestamp = System.currentTimeMillis();
        if (sparseIndex.isEmpty()) {
            return null;
        }
        Pair<String, Integer> indexPair = sparseIndex.getNearestIndexPair(key);
        return null;
    }
}
