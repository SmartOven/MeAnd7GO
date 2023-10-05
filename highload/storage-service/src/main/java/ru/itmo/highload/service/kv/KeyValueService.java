package ru.itmo.highload.service.kv;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KeyValueService {
    private final KeyValueRepository keyValueRepository;
    private final KeyValueMapper keyValueMapper;

    public KeyValueService(
            @Autowired KeyValueRepository keyValueRepository,
            @Autowired KeyValueMapper keyValueMapper
    ) {
        this.keyValueRepository = keyValueRepository;
        this.keyValueMapper = keyValueMapper;
    }

    public KeyValueViewModel get(String key) {
        return keyValueRepository.findByKey(key)
                .map(keyValueMapper::toViewModel)
                .orElse(null);
    }

    public KeyValueViewModel update(KeyValueDto keyValueDto) {
        KeyValueDocument keyValueDocument = keyValueMapper.toDocument(keyValueDto);
        String id = keyValueRepository.findByKey(keyValueDto.getKey())
                .map(KeyValueDocument::getId)
                .orElse(null);
        keyValueDocument.setId(id);
        return keyValueMapper.toViewModel(keyValueRepository.save(keyValueDocument));
    }
}
