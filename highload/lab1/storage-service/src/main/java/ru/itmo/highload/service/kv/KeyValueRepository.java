package ru.itmo.highload.service.kv;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KeyValueRepository extends MongoRepository<KeyValueDocument, String> {
    Optional<KeyValueDocument> findByKey(String key);
}
