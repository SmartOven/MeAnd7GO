package ru.itmo.highload.sharding.sharding;


import ru.itmo.highload.sharding.client.KeyValueDto;
import ru.itmo.highload.sharding.client.KeyValueViewModel;

public interface ShardingService {
    KeyValueViewModel get(String key);
    KeyValueViewModel set(KeyValueDto keyValueDto);
}
