package ru.itmo.highload.sharding.client.redis;

import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.CrudRepository;

@Profile({"redis","redis-cluster"})
public interface KeyValueRedisRepository extends CrudRepository<KeyValue, String> {
}
