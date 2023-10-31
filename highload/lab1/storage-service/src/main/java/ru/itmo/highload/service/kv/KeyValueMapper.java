package ru.itmo.highload.service.kv;

import org.springframework.stereotype.Service;

@Service
public class KeyValueMapper {
    public KeyValueDocument toDocument(KeyValueDto keyValueDto) {
        return new KeyValueDocument(null, keyValueDto.getKey(), keyValueDto.getValue());
    }

    public KeyValueViewModel toViewModel(KeyValueDocument keyValueDocument) {
        return new KeyValueViewModel(keyValueDocument.getKey(), keyValueDocument.getValue());
    }
}
