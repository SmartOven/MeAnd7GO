package ru.itmo.highload.service.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PingController {
    private static final Logger log = LogManager.getLogger();
    @Value("${spring.profiles.active}")
    private String activeProfile;

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        log.info("Pinged");
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/profile")
    public ResponseEntity<String> profile(){
        log.info(activeProfile);
        return ResponseEntity.ok(activeProfile);
    }
}
