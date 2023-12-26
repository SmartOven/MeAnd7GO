package ru.itmo.highload.sharding.client;

public interface KeyValueRepository {
    double getMemUsage();
    String get(String key);
    void set(String key, String value);
}
