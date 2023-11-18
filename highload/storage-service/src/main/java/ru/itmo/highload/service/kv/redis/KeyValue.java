package ru.itmo.highload.service.kv.redis;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("key-value")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KeyValue {
    @Id
    private String key;
    private String value;
}
