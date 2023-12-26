package ru.itmo.highload.sharding.sharding;


import lombok.*;
import lombok.experimental.Accessors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class ShardNode implements Comparable<ShardNode>, Cloneable {
    private String hostname;
    @Accessors(fluent = true)
    private boolean isActive = true;
    private boolean isMaster = true;
    private ShardNode replicaOf = null;
    @Value("${highload.sharding.ping-retry-count:3}")
    private int retryCount;
    @Value("${highload.sharding.ping-node-timeout-ms:3000}")
    private int pingTimeoutMs;
    private LocalDateTime updatedTimestamp = LocalDateTime.MIN;
    private final int hashConst;
    private final RestTemplate restTemplate = new RestTemplate();
    private static final double scaleConst = 1. * (1 << 13) / Integer.MAX_VALUE;
    private static final Logger log = LogManager.getLogger();


    public ShardNode(String hostname) {
        this.hostname = hostname;
        this.hashConst = (int) Math.round(hostname.hashCode() * scaleConst);
        if (replicaOf != null) {
            isMaster = false;
        }
    }

    public void setReplicaOf(ShardNode replicaOf) {
        this.replicaOf = replicaOf;
        isMaster = false;
    }

    @Scheduled(fixedRateString = "${lsm.replication.check-active-node-timeout-ms:1000}")
    public void checkActive() {
        ResponseEntity<String> response;
        LocalTime startPing = LocalTime.now();
        response = restTemplate.getForEntity(String.format("http://%s/api/ping", hostname), String.class);
        while (response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(408)) &&
                retryCount > 0 &&
                startPing.plus(pingTimeoutMs, ChronoUnit.MILLIS).isBefore(LocalTime.now())) {
            response = restTemplate.getForEntity(String.format("http://%s/api/ping", hostname), String.class);
        }

    }

    @Scheduled(fixedRateString = "${lsm.replication.check-active-master-timeout-ms:1000}")
    public void checkMasterActive() {
        //TODO
    }

    @Override
    public int hashCode() {
        return hashConst;
    }

    @Override
    public int compareTo(ShardNode o) {
        return hashConst - o.hashConst;
    }


    @Override
    public ShardNode clone() {
        try {
            ShardNode clone = (ShardNode) super.clone();
            clone.isMaster = isMaster;
            clone.hostname = hostname;
            clone.replicaOf = replicaOf;
            clone.isActive = isActive;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
