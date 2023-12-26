package ru.itmo.highload.service.kv.mappers;

import org.springframework.stereotype.Service;
import ru.itmo.highload.service.kv.KeyValueViewModel;


@Service
public class KeyValueMapper {

    public static KeyValueViewModel toViewModel(String key, String value) {
        return new KeyValueViewModel(key, value);
    }
}
