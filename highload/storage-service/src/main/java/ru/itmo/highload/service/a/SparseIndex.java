package ru.itmo.highload.service.a;

import java.util.Comparator;

/**
 * Карта индексов сегментов SS-таблицы
 */
public class SparseIndex {
    private final SortedPairList<String, Integer> segmentOffsets;

    public SparseIndex() {
        segmentOffsets = new SortedPairList<>(Comparator.comparing(Pair::getKey));
    }

    public void setIndex(String key, Integer offset) {
        segmentOffsets.insertSorted(new Pair<>(key, offset));
    }

    public Integer getNearestIndex(String key) {
        return segmentOffsets.findNearestOrExact(key);
    }
}
