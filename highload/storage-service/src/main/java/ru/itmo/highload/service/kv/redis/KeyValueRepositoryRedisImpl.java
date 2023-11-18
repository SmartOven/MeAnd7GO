package ru.itmo.highload.service.kv.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import ru.itmo.highload.service.kv.KeyValueRepository;

@Repository
@Profile("redis")
public class KeyValueRepositoryRedisImpl implements KeyValueRepository {
    private final KeyValueRedisRepository keyValueRedisRepository;

    public KeyValueRepositoryRedisImpl(@Autowired KeyValueRedisRepository keyValueRedisRepository) {
        this.keyValueRedisRepository = keyValueRedisRepository;
    }

    @Override
    public double getMemUsage() {
        return -2;
    }

    @Override
    public String get(String key) {
        return keyValueRedisRepository
                .findById(key)
                .map(KeyValue::getValue)
                .orElse(null);
    }

    @Override
    public void set(String key, String value) {
        set(new KeyValue(key, value));
    }
    public void set(KeyValue kv){
        keyValueRedisRepository.save(kv);
    }
}
