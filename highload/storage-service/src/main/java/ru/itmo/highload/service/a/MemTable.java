package ru.itmo.highload.service.a;

import java.util.Comparator;
import java.util.TreeMap;

/**
 * Красно-черное дерево с парами key=value
 */
public class MemTable {
    /**
     * TreeMap в Java реализован на красно-черном дереве
     */
    private final TreeMap<String, Integer> treeMap;
    private long memSize;

    public MemTable() {
        this.treeMap = new TreeMap<>(Comparator.naturalOrder());
        this.memSize = 0;
    }

    public Integer get(String key) {
        return treeMap.get(key);
    }

    public void set(String key, Integer value) {
        treeMap.put(key, value);
        memSize += key.length() * 2L; // key
        memSize += 4; // value
    }

    public long getMemSize() {
        return memSize;
    }
}
