package ru.itmo.highload.service.kv;

import org.springframework.stereotype.Repository;

@Repository
public interface KeyValueRepository {
    double getMemUsage();
    String get(String key);
    void set(String key, String value);
}
