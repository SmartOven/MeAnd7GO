package ru.itmo.highload.service.a;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class KeyValueService {
    private final MemTable memTable;
    private final long memTableSizeBytes;
//    private final

    public KeyValueService(
            @Value("${mem-table.size.mib:10}") int memTableSizeMib,
            @Value("${ss-table.compaction.timeout.minutes:1}") int sstableCompactionTimeout) {
        this.memTable = new MemTable();
        this.memTableSizeBytes = memTableSizeMib * 1048576L;
    }

    public String get(String key) {
        // TODO
        //  ищем в MemTable, если там нет - ищем в SS-таблицах на диске
        //  При поиске по SS-таблицам смотрим на разреженный индекс, по нему находим нужную
        //  SS-таблицу (и нужный сегмент), этот сегмент кладем в оперативку,
        //  разжимаем и добываем нужное значение
        return null;
    }

    public void set(String key, String value) {
        // TODO
        //  пишем в MemTable, если ее размер превысил допустимый - собираем из нее SsTable
        //  и пишем ее на диск (вопрос: как записывать SS-таблицу с ключом key1,
        //  если на диске уже может быть таблица с таким ключом? наверное,
        //  придется проверить соответствующую таблицу
        //  (найти подходящую по разреженному индексу)
        //  и проверить наличие ключа в ней (для всех ключей в MemTable))
    }

    // TODO
    //  По крону запускать слияние и уплотнение ss-таблиц на диске
    //  Уплотнять нужно SS-таблицы по сегментам, при этом обновлять разреженный индекс
    //  (так как в таком случае все offset съедут)
}
