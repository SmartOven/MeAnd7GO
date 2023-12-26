package ru.itmo.highload.sharding.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.itmo.highload.sharding.sharding.ConsistentHashing;

import java.util.List;

@Getter
@Configuration
public class ShardingConfig {
    @Value("${highload.sharding.lsm.master-hosts:}")
    private List<String> masterHosts;
    @Value("${highload.sharding.lsm.replica-hosts:}")
    private List<String> replicaHosts;
    @Value("${highload.sharding.lsm.configuration.count-virtual-nodes:0}")
    private int countVirtualNodes;
    @Value("${highload.sharding.lsm.configuration.ping-node-timeout-ms:3000}")
    private int pingTimeoutMs;
    @Value("${highload.sharding.lsm.configuration.ping-retry-count:3}")
    private int retryNumberPingNode;

    @Bean
    public ConsistentHashing consistentHashing() {
        return new ConsistentHashing(countVirtualNodes);
    }
}

