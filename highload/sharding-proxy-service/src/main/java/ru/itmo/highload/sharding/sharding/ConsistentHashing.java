package ru.itmo.highload.sharding.sharding;

import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.IntStream;

public class ConsistentHashing {
    private final TreeMap<Integer, ShardNode> ring = new TreeMap<>();
    private final TreeMap<Integer, ShardNode> replicasRing = new TreeMap<>();
    private static final double scaleConst = 1. * (1 << 13) / Integer.MAX_VALUE;
    private final HashCalculator hashCalculator;
    private final int numberOfVirtualNodes;

    public ConsistentHashing(int numberOfVirtualNodes) {
        this.numberOfVirtualNodes = numberOfVirtualNodes;
        this.hashCalculator = String::hashCode;
    }

    public ConsistentHashing(int numberOfVirtualNodes, HashCalculator hashCalculator) {
        this.numberOfVirtualNodes = numberOfVirtualNodes;
        this.hashCalculator = hashCalculator;
    }

    public void addShardNode(ShardNode node) {
        IntStream dontUseForEachLoops = IntStream.range(0, numberOfVirtualNodes + 1);
        if (node.isMaster()) {
            dontUseForEachLoops
                    .forEach(i -> ring.put(calculateScaleHash("%s#%d".formatted(node.getHostname(), i)), node));
        } else {
            dontUseForEachLoops
                    .forEach(i -> replicasRing.put(calculateScaleHash("%s#%d".formatted(node.getReplicaOf().getHostname(), i)), node));
        }
    }


    public ShardNode getShardNodeForGet(String key) {
        return getShardNode(key, replicasRing);
    }

    public ShardNode getShardNodeForSet(String key) {
        return getShardNode(key, ring);
    }

    private ShardNode getShardNode(String key, TreeMap<Integer, ShardNode> ring) {
        if (ring.isEmpty())
            return null;
        int hash = calculateScaleHash(key);
        if (ring.containsKey(hash))
            return ring.get(hash);
        Integer shardKey = ring.ceilingKey(hash);
        return shardKey != null ? ring.get(shardKey) : ring.firstEntry().getValue();
    }

    public void removeShardNode(ShardNode node) {
        for (int i = 0; i < numberOfVirtualNodes; i++) {
            ring.remove(calculateScaleHash(node.getHostname() + i));
        }
    }

    public int calculateScaleHash(String hostname) {
        return (int) Math.round(hashCalculator.calculate(hostname) * scaleConst);
    }

    public List<ShardNode> getShardNodes() {
        return new HashSet<>(ring.values()).stream().toList();
    }
}
