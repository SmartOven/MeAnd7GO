package ru.itmo.highload.service.kv;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.itmo.highload.service.kv.mongo.KeyValueMapper;

@Service
public class KeyValueService {
    private final KeyValueRepository keyValueRepository;


    public KeyValueService(@Autowired KeyValueRepository keyValueRepository) {
        this.keyValueRepository = keyValueRepository;
    }


    public KeyValueViewModel get(String key) {
        return new KeyValueViewModel(key, keyValueRepository.get(key));
    }

    public KeyValueViewModel update(KeyValueDto keyValueDto) {
        String key = keyValueDto.getKey();
        String value = keyValueDto.getValue();
        keyValueRepository.set(key, value);
        return KeyValueMapper.toViewModel(key, value);
    }

    public double getMemUsage() {
        return keyValueRepository.getMemUsage();
    }
}
