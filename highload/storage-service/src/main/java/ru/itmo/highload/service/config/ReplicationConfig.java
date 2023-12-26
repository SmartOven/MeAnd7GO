package ru.itmo.highload.service.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class ReplicationConfig {
    @Value("${lsm.replication.replicaof.ip:}")
    private String replicaOfIp;
    @Value("${lsm.replication.replicaof.port:}")
    private String replicaOfPort;
    @Value("${lsm.replication.interval-ms:10000}")
    private Integer replicationIntervalMs;
}
