package ru.itmo.highload.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
