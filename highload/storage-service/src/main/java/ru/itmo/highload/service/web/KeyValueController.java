package ru.itmo.highload.service.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.highload.service.kv.KeyValueDto;

@RestController
@RequestMapping("/api")
public class KeyValueController {
    @GetMapping("/get")
    public ResponseEntity<?> get(@RequestParam String key) {
        return null;
    }
    @PostMapping("/set")
    public ResponseEntity<?> set(@RequestBody KeyValueDto keyValueDto) {
        return null;
    }
}
