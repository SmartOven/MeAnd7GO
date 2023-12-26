package ru.itmo.highload.sharding.client.mongo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import ru.itmo.highload.sharding.client.KeyValueRepository;


@Repository
@Profile("mongo")
public class KeyValueRepositoryMongoImpl implements KeyValueRepository {
    private final KeyValueMongoRepository keyValueMongoRepository;

    public KeyValueRepositoryMongoImpl(@Autowired KeyValueMongoRepository keyValueMongoRepository) {
        this.keyValueMongoRepository = keyValueMongoRepository;
    }

    @Override
    public double getMemUsage() {
        return -1;
    }

    @Override
    public String get(String key) {
        return keyValueMongoRepository.findByKey(key)
                .map(KeyValueDocument::getValue)
                .orElse(null);
    }

    @Override
    public void set(String key, String value) {
        String id = keyValueMongoRepository.findByKey(key)
                .map(KeyValueDocument::getId)
                .orElse(null);
        set(KeyValueMapper.toDocument(id, key, value));
    }

    public KeyValueDocument set(KeyValueDocument keyValueDocument) {
        return keyValueMongoRepository.save(keyValueDocument);
    }


}
