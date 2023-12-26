package ru.itmo.highload.sharding.client.mongo;

import org.springframework.stereotype.Service;
import ru.itmo.highload.sharding.client.KeyValueDto;
import ru.itmo.highload.sharding.client.KeyValueViewModel;


@Service
public class KeyValueMapper {


    public static KeyValueDocument toDocument(KeyValueDto keyValueDto) {
        return toDocument(keyValueDto.getKey(), keyValueDto.getValue());
    }

    public static KeyValueViewModel toViewModel(KeyValueDocument keyValueDocument) {
        return toViewModel(keyValueDocument.getKey(), keyValueDocument.getValue());
    }

    public static KeyValueDocument toDocument(String id, String key, String value) {
        return new KeyValueDocument(id, key, value);
    }

    public static KeyValueDocument toDocument(String key, String value) {
        return toDocument(null, key, value);
    }

    public static KeyValueViewModel toViewModel(String key, String value) {
        return new KeyValueViewModel(key, value);
    }
}
