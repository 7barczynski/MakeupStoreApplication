package com.tbar.MakeupStoreApplication.service.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Slf4j
@Component
public class APIConsumerImpl<T> implements APIConsumer<T> {

    // === fields ===
    private RestTemplate restTemplate;

    // === constructors ===
    @Autowired
    public APIConsumerImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // === public methods ===

    /**
     *
     * Send {@code HttpMethod.GET} request and get response from an external API as the ResponseEntity. <br>
     *
     * If {@code RestTemplate} {@link RestTemplate#exchange exchange()} method throws
     * {@code RestClientException} then the returned entity is
     * with {@code null} body and with {@code HttpStatusCode.SERVICE_UNAVAILABLE} (503).
     *
     * @param uri URI of the request to the API. Can't be {@code null}.
     * @return response from API as an entity
     */
    @Override
    public ResponseEntity<T> requestData(@NonNull URI uri) {
        ResponseEntity<T> response;
        try {
            response = restTemplate.exchange(uri, HttpMethod.GET, null,
                    new ParameterizedTypeReference<>() {});
            log.debug("Response taken from external API. Status code = {}; URI = {}; Body = {}", response.getStatusCode(), uri, response.getBody());
        } catch (RestClientException e) {
            log.error("RestClientException reached. Returning empty ResponseEntity with status code 503. Exception message = {}", e.getMessage());
            response = new ResponseEntity<>(null, HttpStatus.SERVICE_UNAVAILABLE);
        }
        return response;
    }
}
