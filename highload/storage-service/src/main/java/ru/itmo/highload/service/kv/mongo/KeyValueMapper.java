package ru.itmo.highload.service.kv.mongo;

import org.springframework.stereotype.Service;
import ru.itmo.highload.service.kv.KeyValueDto;
import ru.itmo.highload.service.kv.KeyValueViewModel;

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
