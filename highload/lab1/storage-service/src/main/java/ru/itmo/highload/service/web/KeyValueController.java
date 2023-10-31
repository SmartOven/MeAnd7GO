package ru.itmo.highload.service.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.highload.service.kv.KeyValueDto;
import ru.itmo.highload.service.kv.KeyValueService;
import ru.itmo.highload.service.kv.KeyValueViewModel;

@RestController
@RequestMapping("/api")
public class KeyValueController {
    private final KeyValueService keyValueService;

    public KeyValueController(@Autowired KeyValueService keyValueService) {
        this.keyValueService = keyValueService;
    }

    @GetMapping("/get")
    public ResponseEntity<?> get(@RequestParam String key) {
        KeyValueViewModel keyValue = keyValueService.get(key);
        return ResponseEntity.ok(keyValue);
    }

    @PostMapping("/set")
    public ResponseEntity<?> set(@RequestBody KeyValueDto keyValueDto) {
        KeyValueViewModel keyValue = keyValueService.update(keyValueDto);
        return ResponseEntity.ok(keyValue);
    }
}
