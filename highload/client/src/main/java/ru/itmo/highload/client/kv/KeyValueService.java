package ru.itmo.highload.client.kv;

import java.util.List;

import org.apache.commons.logging.Log;
import org.springframework.http.HttpLogging;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class KeyValueService {
    private static final Log log = HttpLogging.forLogName(KeyValueService.class);
    private static final String URL_GET = "http://10.5.0.3:8080/api/get";
    private static final String URL_SET = "http://10.5.0.3:8080/api/set";
    private final RestTemplate restTemplate;

    public KeyValueService() {
        restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        messageConverters.add(converter);
        restTemplate.setMessageConverters(messageConverters);
    }

    public KeyValueViewModel get(String key) {
        String url = URL_GET + "?key=" + key;
        log.info(String.format("Sending request on %s", url));
        ResponseEntity<KeyValueViewModel> response = restTemplate.getForEntity(url, KeyValueViewModel.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Request failed with status code: " + response.getStatusCode());
            return null;
        }
        return response.getBody();
    }

    public KeyValueViewModel set(KeyValueDto keyValueDto) {
        log.info(String.format("Sending request on %s with body=%s", URL_SET, keyValueDto));
        RequestEntity<KeyValueDto> request = RequestEntity.post(URL_SET).body(keyValueDto);
        ResponseEntity<KeyValueViewModel> response = restTemplate.exchange(request, KeyValueViewModel.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Request failed with status code: " + response.getStatusCode());
            return null;
        }
        return response.getBody();
    }
}
