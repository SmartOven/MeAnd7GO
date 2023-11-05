package ru.itmo.highload.service.kv.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SortedPairList<Key, Value> extends ArrayList<Pair<Key, Value>> {
    private final Comparator<Pair<Key, Value>> comparator;

    public SortedPairList(Comparator<Pair<Key, Value>> comparator) {
        this.comparator = comparator;
    }

    public void insertSorted(Pair<Key, Value> pair) {
        int i = Collections.binarySearch(this, pair, comparator);
        add(i < 0 ? -i - 1 : i, pair);
    }

    public Pair<Key, Value> findNearestOrExact(Key key) {
        int i = Collections.binarySearch(this, new Pair<>(key, null), comparator);
        if (i < 0) {
            return get(Math.max(0, Math.min(-i - 1, size() - 1)));
        }
        return get(Math.max(0, Math.min(i, size() - 1)));
    }
}
