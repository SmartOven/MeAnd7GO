package ru.itmo.highload.sharding.sharding;

import jakarta.annotation.PostConstruct;
import org.apache.commons.logging.Log;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpLogging;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.itmo.highload.sharding.client.KeyValueDto;
import ru.itmo.highload.sharding.client.KeyValueViewModel;
import ru.itmo.highload.sharding.config.ShardingConfig;

@Profile("lsm-sharding-consistent")
@Service
public class ShardingServiceConsistentHashImpl implements ShardingService {
    private static final Log log = HttpLogging.forLogName(ShardingServiceConsistentHashImpl.class);

    private final ConsistentHashing consistentHashing;
    private final ShardingConfig shardingConfig;
    private final RestTemplate restTemplate = new RestTemplate();

    public ShardingServiceConsistentHashImpl(ShardingConfig shardingConfig, ConsistentHashing consistentHashing) {
        this.shardingConfig = shardingConfig;
        this.consistentHashing = consistentHashing;


    }

    @PostConstruct
    public void init() {
        log.info("init PostConstruct");
        var countVirtualNodes = shardingConfig.getCountVirtualNodes();
        log.info(shardingConfig.getMasterHosts());
        for (int i = 0; i < shardingConfig.getMasterHosts().size(); i++) {
            var newMaster = new ShardNode(shardingConfig.getMasterHosts().get(i));
            var newReplica = new ShardNode(shardingConfig.getReplicaHosts().get(i));
            newReplica.setReplicaOf(newMaster);

            consistentHashing.addShardNode(newMaster);
            consistentHashing.addShardNode(newReplica);
        }
    }

    @Scheduled(fixedRateString = "${highload.sharding.ping-node-timeout-ms:10000}")
    public void pingNodes() {
        consistentHashing.getShardNodes().stream()
                .filter(shardNode -> !shardNode.isActive() && shardNode.isMaster())
                .forEach(consistentHashing::removeShardNode);
    }

    @Override
    public KeyValueViewModel get(String key) {
        String url = String.format("http://%s/api/get?key=%s", consistentHashing.getShardNodeForGet(key).getHostname(), key);
        ResponseEntity<KeyValueViewModel> response = restTemplate.getForEntity(url, KeyValueViewModel.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Request failed with status code: " + response.getStatusCode());
            return null;
        }
        return response.getBody();
    }

    @Override
    public KeyValueViewModel set(KeyValueDto keyValueDto) {
        String url = String.format("http://%s/api/set", consistentHashing.getShardNodeForSet(keyValueDto.getKey()).getHostname());
        RequestEntity<KeyValueDto> request = RequestEntity.post(url).body(keyValueDto);
        ResponseEntity<KeyValueViewModel> response = restTemplate.exchange(request, KeyValueViewModel.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Request failed with status code: " + response.getStatusCode());
            return null;
        }
        return response.getBody();
    }
}
