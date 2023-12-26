package ru.itmo.highload.sharding.sharding;

import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpLogging;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.itmo.highload.sharding.client.KeyValueDto;
import ru.itmo.highload.sharding.client.KeyValueViewModel;
import ru.itmo.highload.sharding.config.ShardingConfig;


@Profile("lsm-sharding-simple")
@Service
public class ShardingServiceImpl implements ShardingService {
    private static final Log log = HttpLogging.forLogName(ShardingServiceImpl.class);
    private final ShardingConfig shardingConfig;
    private final RestTemplate restTemplate = new RestTemplate();

    public ShardingServiceImpl(@Autowired ShardingConfig shardingConfig) {
        this.shardingConfig = shardingConfig;
    }

    public String getShardHostForGetByKey(String key) {
        var countHosts = shardingConfig.getReplicaHosts().size();
        return countHosts > 0 ? shardingConfig.getReplicaHosts().get(key.hashCode() % countHosts)
                : getShardHostForPutOrPostByKey(key);
    }

    public String getShardHostForPutOrPostByKey(String key) {
        var countHosts = shardingConfig.getMasterHosts().size();

        return countHosts > 0 ? shardingConfig.getMasterHosts().get(key.hashCode() % countHosts) : null;
    }

    @Override
    public KeyValueViewModel get(String key) {
        String host = getShardHostForGetByKey(key);
        if (host == null) {
            log.info("shard for key=%s doesn't exist".formatted(key));
            return null;
        }
        String url = String.format("http://%s/api/get", host);
        ResponseEntity<KeyValueViewModel> response = restTemplate.getForEntity(url, KeyValueViewModel.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Request failed with status code: " + response.getStatusCode());
            return null;
        }
        return response.getBody();
    }

    @Override
    public KeyValueViewModel set(KeyValueDto keyValueDto) {
        String host = getShardHostForPutOrPostByKey(keyValueDto.getKey());
        if (host == null) {
            log.info("shard for key=%s doesn't exist".formatted(keyValueDto.getKey()));
            return null;
        }
        String url = String.format("http://%s/api/get", host);
        RequestEntity<KeyValueDto> request = RequestEntity.post(url).body(keyValueDto);
        ResponseEntity<KeyValueViewModel> response = restTemplate.exchange(request, KeyValueViewModel.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Request failed with status code: " + response.getStatusCode());
            return null;
        }
        return response.getBody();
    }
}
