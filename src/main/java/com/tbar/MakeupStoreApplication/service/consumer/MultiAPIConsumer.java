package com.tbar.MakeupStoreApplication.service.consumer;

import com.tbar.MakeupStoreApplication.service.consumer.model.Item;
import com.tbar.MakeupStoreApplication.utility.exceptions.APICallException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

@Slf4j
@Component
public class MultiAPIConsumer implements APIConsumer {

    // === fields ===
    private RestTemplate restTemplate;

    // === constructors ===
    @Autowired
    public MultiAPIConsumer(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // === public methods ===
    /**
     *
     * Send {@code HttpMethod.GET} request and get response from an external API as the ResponseEntity.
     *
     * @param uri URI of the request to the API. Can't be {@code null}.
     * @throws APICallException when nothing has been found or there is client or server error.
     * @return response from API as an entity
     */
    @Override
    public ResponseEntity<List<Item>> requestData(@NonNull URI uri) throws APICallException {
        ResponseEntity<List<Item>> response = restTemplate.exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
        log.debug("Response taken from external API. Status code = {}; URI = {}; Body = {}", response.getStatusCode(), uri, response.getBody());
        return response;
    }
}
