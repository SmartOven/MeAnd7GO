package ru.itmo.highload.service.kv;

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
        return get(i);
    }
}
