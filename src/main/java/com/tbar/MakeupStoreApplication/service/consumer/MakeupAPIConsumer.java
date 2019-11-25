package com.tbar.MakeupStoreApplication.service.consumer;

import com.tbar.MakeupStoreApplication.service.consumer.model.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;

@Slf4j
@Component
public class MakeupAPIConsumer implements APIConsumer<List<Item>> {

    // === fields ===
    private RestTemplate restTemplate;

    // === constructors ===
    @Autowired
    public MakeupAPIConsumer(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // === public methods ===

    @Override
    public ResponseEntity<List<Item>> requestData(@NotNull URI uri) {
        if (uri == null) {
            log.error("Request URI to external API is null. Returning empty ResponseEntity with status code 500.");
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        ResponseEntity<List<Item>> response;
        try {
            response = restTemplate.exchange(uri, HttpMethod.GET, null,
                    new ParameterizedTypeReference<>() {});
            log.debug("Response taken from external API. Status code = {}; URI = {}; List = {}", response.getStatusCode(), uri, response.getBody());
        } catch (RestClientException e) {
            log.info("RestClientException reached. Returning empty ResponseEntity with status code 503. Exception message = {}", e.getMessage());
            response = new ResponseEntity<>(null, HttpStatus.SERVICE_UNAVAILABLE);
        }
        return response;
    }
}
