package ru.itmo.highload.sharding.client.mongo;

import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

@Profile("mongo")
public interface KeyValueMongoRepository extends MongoRepository<KeyValueDocument, String>{
    Optional<KeyValueDocument> findByKey(String key);
}
