package ru.itmo.highload.service.kv.lsm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.itmo.highload.service.config.ReplicationConfig;
import ru.itmo.highload.service.kv.lsm.storage.MemTableVersioned;

@Component
public class ReplicationComponent {

    private static final Logger log = LogManager.getLogger();
    private final RestTemplate restTemplate = new RestTemplate();
    private final ReplicationConfig replicationConfig;
    private String walUrl;

    public ReplicationComponent(ReplicationConfig replicationConfig) {
        this.replicationConfig = replicationConfig;
        this.walUrl = String.format("http://%s%s/api/replication/wal",
                replicationConfig.getReplicaOfIp(),
                replicationConfig.getReplicaOfPort().isBlank() ?
                        "" :
                        ":" + replicationConfig.getReplicaOfPort());
    }

    public MemTableVersioned requestWal() {
        var replicaOfIp = this.replicationConfig.getReplicaOfIp();
        if (replicaOfIp == null || replicaOfIp.isEmpty() || replicaOfIp.isBlank()) {
            return null;
        }
        if (walUrl == null)
            this.walUrl = String.format("http://%s%s/api/replication/wal",
                    replicationConfig.getReplicaOfIp(),
                    replicationConfig.getReplicaOfPort().isBlank() ?
                            "" :
                            ":" + replicationConfig.getReplicaOfPort());

        log.info("wal table requested");

        ResponseEntity<MemTableVersioned> response = restTemplate.getForEntity(walUrl, MemTableVersioned.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            log.warn("wtf ");
            return null;
        }
        log.info("wal table downloaded");
        return response.getBody();
    }
}
