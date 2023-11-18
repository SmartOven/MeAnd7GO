package ru.itmo.highload.service.kv.lsm;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.springframework.lang.NonNull;
import ru.itmo.highload.service.kv.util.Pair;
import ru.itmo.highload.service.kv.util.SortedPairList;
import ru.itmo.highload.service.kv.util.SparseIndexSerializable;

/**
 * Карта индексов сегментов SS-таблицы
 */
public class SparseIndex implements List<Pair<String, Long>> {
    private final SortedPairList<String, Long> segmentOffsets;

    public SparseIndex() {
        segmentOffsets = new SortedPairList<>(Comparator.comparing(Pair::getKey));
    }

    public void setIndex(MemTable memTable, long offset) {
        segmentOffsets.insertSorted(new Pair<>(memTable.firstKey(), offset));
    }

    public Pair<String, Long> getNearestIndexPair(String key) {
        return segmentOffsets.findNearestOrExact(key);
    }

    public SparseIndexSerializable toSerializable() {
        return new SparseIndexSerializable(segmentOffsets);
    }

    public static SparseIndex ofSerializable(SparseIndexSerializable sparseIndexSerializable) {
        SparseIndex sparseIndex = new SparseIndex();
        sparseIndex.addAll(sparseIndexSerializable.getSegmentOffsets());
        return sparseIndex;
    }

    @Override
    public int size() {
        return segmentOffsets.size();
    }

    @Override
    public boolean isEmpty() {
        return segmentOffsets.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return segmentOffsets.contains(o);
    }

    @Override
    public Iterator<Pair<String, Long>> iterator() {
        return segmentOffsets.iterator();
    }

    @Override
    public Object[] toArray() {
        return segmentOffsets.toArray();
    }

    @Override
    public <T> T[] toArray(@NonNull T[] a) {
        return segmentOffsets.toArray(a);
    }

    @Override
    public boolean add(Pair<String, Long> stringLongPair) {
        return segmentOffsets.add(stringLongPair);
    }

    @Override
    public boolean remove(Object o) {
        return segmentOffsets.remove(o);
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> c) {
        return segmentOffsets.containsAll(c);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends Pair<String, Long>> c) {
        return segmentOffsets.addAll(c);
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends Pair<String, Long>> c) {
        return segmentOffsets.addAll(index, c);
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        return segmentOffsets.removeAll(c);
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        return segmentOffsets.retainAll(c);
    }

    @Override
    public void clear() {
        segmentOffsets.clear();
    }

    @Override
    public Pair<String, Long> get(int index) {
        return segmentOffsets.get(index);
    }

    @Override
    public Pair<String, Long> set(int index, Pair<String, Long> element) {
        return segmentOffsets.set(index, element);
    }

    @Override
    public void add(int index, Pair<String, Long> element) {
        segmentOffsets.add(index, element);
    }

    @Override
    public Pair<String, Long> remove(int index) {
        return segmentOffsets.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return segmentOffsets.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return segmentOffsets.lastIndexOf(o);
    }

    @Override
    public ListIterator<Pair<String, Long>> listIterator() {
        return segmentOffsets.listIterator();
    }

    @Override
    public ListIterator<Pair<String, Long>> listIterator(int index) {
        return segmentOffsets.listIterator(index);
    }

    @Override
    public List<Pair<String, Long>> subList(int fromIndex, int toIndex) {
        return segmentOffsets.subList(fromIndex, toIndex);
    }
}
