package ru.itmo.highload.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.net.URI;
import java.util.Scanner;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class Main {
    private static final Logger log = LogManager.getLogger();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        RestTemplate restTemplate = new RestTemplate();
        URI uri = URI.create("http://localhost:8080/api/ping");
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.equals("exit")) {
                log.info("Exiting...");
                break;
            }
            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                String responseBody = response.getBody();
                log.info("Response: " + responseBody);
            } else {
                log.info("Request failed with status code: " + response.getStatusCode());
            }
        }
    }
}