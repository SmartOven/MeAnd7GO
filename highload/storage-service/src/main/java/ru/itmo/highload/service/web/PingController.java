package ru.itmo.highload.service.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PingController {
    private static final Logger log = LogManager.getLogger();

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        log.info("Pinged");
        return ResponseEntity.ok("OK");
    }
}
