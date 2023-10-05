package ru.itmo.highload.service.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.highload.service.kv.KeyValueDocument;
import ru.itmo.highload.service.kv.KeyValueDto;
import ru.itmo.highload.service.kv.KeyValueService;

@RestController
@RequestMapping("/api")
public class KeyValueController {
    private final KeyValueService keyValueService;

    public KeyValueController(@Autowired KeyValueService keyValueService) {
        this.keyValueService = keyValueService;
    }

    @GetMapping("/get")
    public ResponseEntity<?> get(@RequestParam String key) {
        KeyValueDocument keyValueDocument = keyValueService.get(key);
        return ResponseEntity.ok(keyValueDocument);
    }

    @PostMapping("/set")
    public ResponseEntity<?> set(@RequestBody KeyValueDto keyValueDto) {
        KeyValueDocument keyValueDocument = keyValueService.update(keyValueDto);
        return ResponseEntity.ok(keyValueDocument);
    }
}
