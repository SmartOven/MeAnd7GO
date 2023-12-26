package ru.itmo.highload.sharding.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.highload.sharding.client.KeyValueDto;
import ru.itmo.highload.sharding.client.KeyValueViewModel;
import ru.itmo.highload.sharding.sharding.ShardingService;

@RestController
@RequiredArgsConstructor
public class ProxyController {
    private final ShardingService shardingService;

    @GetMapping("/get")
    public ResponseEntity<?> get(@RequestParam String key) {
        KeyValueViewModel keyValue = shardingService.get(key);
        return ResponseEntity.ok(keyValue);
    }

    @PostMapping("/set")
    public ResponseEntity<?> set(@RequestBody KeyValueDto keyValueDto) {
        KeyValueViewModel keyValue = shardingService.set(keyValueDto);
        return ResponseEntity.ok(keyValue);
    }

}
