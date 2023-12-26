package ru.itmo.highload.sharding.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ru.itmo.highload.sharding.client.mongo.KeyValueMapper;
import ru.itmo.highload.sharding.sharding.ShardingService;

@Profile({"mongo", "redis", "cluster", "redis-cluster"})
@Service
public class KeyValueService implements ShardingService {
    private final KeyValueRepository keyValueRepository;


    public KeyValueService(@Autowired KeyValueRepository keyValueRepository) {
        this.keyValueRepository = keyValueRepository;
    }


    public KeyValueViewModel get(String key) {
        return new KeyValueViewModel(key, keyValueRepository.get(key));
    }

    public KeyValueViewModel set(KeyValueDto keyValueDto) {
        String key = keyValueDto.getKey();
        String value = keyValueDto.getValue();
        keyValueRepository.set(key, value);
        return KeyValueMapper.toViewModel(key, value);
    }
}
