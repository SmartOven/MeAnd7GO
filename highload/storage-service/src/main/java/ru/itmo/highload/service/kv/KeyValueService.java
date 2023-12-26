package ru.itmo.highload.service.kv;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.itmo.highload.service.config.ReplicationConfig;
import ru.itmo.highload.service.kv.lsm.*;
import ru.itmo.highload.service.kv.lsm.ReplicationComponent;
import ru.itmo.highload.service.kv.lsm.storage.MemTableVersioned;
import ru.itmo.highload.service.kv.mappers.KeyValueMapper;

@Service
public class KeyValueService {
    private final KeyValueRepository keyValueRepository;

    private final ReplicationConfig replicationConfig;
    private final ReplicationComponent replicationComponent;
    private long walVersion = 0;

    public KeyValueService(KeyValueRepository keyValueRepository,
                           ReplicationConfig replicationConfig,
                           ReplicationComponent replicationComponent) {
        this.keyValueRepository = keyValueRepository;
        this.replicationConfig = replicationConfig;
        this.replicationComponent = replicationComponent;
    }


    public KeyValueViewModel get(String key) {
        return new KeyValueViewModel(key, keyValueRepository.get(key));
    }

    public KeyValueViewModel update(KeyValueDto keyValueDto) {
        String key = keyValueDto.getKey();
        String value = keyValueDto.getValue();
        var replicaOf = replicationConfig.getReplicaOfIp();
        if (replicaOf != null && !replicaOf.isEmpty() && !replicaOf.isBlank())
            throw new IllegalArgumentException("Cannot set key in read-only replica");
//        if (!shardingService.checkKeyRange(key))
//            throw new IllegalArgumentException("Section range doesn't contain key=" + key);
        keyValueRepository.set(key, value);
        return KeyValueMapper.toViewModel(key, value);
    }

    public double getMemUsage() {
        return keyValueRepository.getMemUsage();
    }


    public MemTableVersioned getVersionedMemTableWal() {
        return keyValueRepository.getMemTableVersionedWal();
    }

    @Async
    @Profile("replication-lsm")
    @Scheduled(fixedRateString = "${lsm.replication.interval-ms:10000}")
    public void scheduledDownloadWalFromMaster() {
        var replicaOf = replicationConfig.getReplicaOfIp();
        if (replicaOf == null || replicaOf.isEmpty() || replicaOf.isBlank()) {
            return;
        }
        MemTableVersioned wal = replicationComponent.requestWal();
        if (wal != null && walVersion < wal.lastModified() && wal.memTable() != null) {
            keyValueRepository.addWalMemTable(wal.memTable());
            //TODO save in file
            walVersion = wal.lastModified();
        }
    }


}
