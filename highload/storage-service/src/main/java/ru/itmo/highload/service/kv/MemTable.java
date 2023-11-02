package ru.itmo.highload.service.kv;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.lang.NonNull;

/**
 * Красно-черное дерево с парами key=value
 */
public class MemTable implements Serializable, Map<String, String> {
    /**
     * TreeMap в Java реализован на красно-черном дереве
     */
    private final TreeMap<String, String> treeMap;
    private long memSize;

    public MemTable() {
        this.treeMap = new TreeMap<>(Comparator.naturalOrder());
        this.memSize = 0;
    }
    public void set(String key, String value) {
        treeMap.put(key, value);
        memSize += key.length() * 2L; // key
        memSize += value.length() * 2L; // value
    }

    public long getMemSize() {
        return memSize;
    }

    public String firstKey() {
        return treeMap.firstKey();
    }

    public void putAllIfAbsent(Map<? extends String, ? extends String> m) {
        for (Entry<? extends String, ? extends String> entry : m.entrySet()) {
            if (treeMap.containsKey(entry.getKey())) {
                continue;
            }
            treeMap.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public int size() {
        return treeMap.size();
    }

    @Override
    public boolean isEmpty() {
        return treeMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return treeMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return treeMap.containsValue(value);
    }

    @Override
    public String get(Object key) {
        return treeMap.get(key);
    }

    @Override
    public String put(String key, String value) {
        if (!treeMap.containsKey(key)) {
            memSize += key.length() * 2L; // key
            memSize += value.length() * 2L; // value
        }
        return treeMap.put(key, value);
    }

    @Override
    public String remove(Object key) {
        return treeMap.remove(key);
    }

    @Override
    public void putAll(@NonNull Map<? extends String, ? extends String> m) {
        treeMap.putAll(m);
    }

    @Override
    public void clear() {
        treeMap.clear();
    }

    @Override
    public Set<String> keySet() {
        return treeMap.keySet();
    }

    @Override
    public Collection<String> values() {
        return treeMap.values();
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        return treeMap.entrySet();
    }
}
