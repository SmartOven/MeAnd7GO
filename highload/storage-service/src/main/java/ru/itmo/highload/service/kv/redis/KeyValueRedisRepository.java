package ru.itmo.highload.service.kv.redis;

import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.CrudRepository;

@Profile("redis")
public interface KeyValueRedisRepository extends CrudRepository<KeyValue, String> {
}
