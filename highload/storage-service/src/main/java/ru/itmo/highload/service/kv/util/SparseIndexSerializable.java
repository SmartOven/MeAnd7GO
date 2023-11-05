package ru.itmo.highload.service.kv.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SparseIndexSerializable implements Serializable {
    private final List<Pair<String, Long>> segmentOffsets;

    public SparseIndexSerializable(SortedPairList<String, Long> segmentOffsets) {
        this.segmentOffsets = new ArrayList<>(segmentOffsets);
    }

    public List<Pair<String, Long>> getSegmentOffsets() {
        return segmentOffsets;
    }
}
