package ru.itmo.highload.client.web;

import java.util.Map;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.itmo.highload.client.Logger;
import ru.itmo.highload.client.kv.KeyValueDto;
import ru.itmo.highload.client.kv.KeyValueViewModel;

public class KeyValueService {
    private static final Logger log = Logger.getInstance();
    private static final String URL_GET = "http://localhost:8080/api/get";
    private static final String URL_SET = "http://localhost:8080/api/set";
    private final RestTemplate restTemplate;

    public KeyValueService() {
        restTemplate = new RestTemplate();
    }

    public KeyValueViewModel get(String key) {
        ResponseEntity<KeyValueViewModel> response = restTemplate.getForEntity(
                URL_GET,
                KeyValueViewModel.class,
                Map.of("key", key)
        );
        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Request failed with status code: " + response.getStatusCode());
            return null;
        }
        return response.getBody();
    }

    public KeyValueViewModel set(KeyValueDto keyValueDto) {
        RequestEntity<KeyValueDto> request = RequestEntity.post(URL_SET).body(keyValueDto);
        ResponseEntity<KeyValueViewModel> response = restTemplate.exchange(request, KeyValueViewModel.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Request failed with status code: " + response.getStatusCode());
            return null;
        }
        return response.getBody();
    }
}
