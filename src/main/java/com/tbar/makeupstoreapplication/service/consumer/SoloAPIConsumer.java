package com.tbar.makeupstoreapplication.service.consumer;

import com.tbar.makeupstoreapplication.service.consumer.model.Item;
import com.tbar.makeupstoreapplication.utility.exceptions.consumerlayer.APICallException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Slf4j
@Component
public class SoloAPIConsumer implements APIConsumer {

    // === fields ===
    private RestTemplate restTemplate;

    // === constructors ===
    @Autowired
    public SoloAPIConsumer(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // === public methods ===
    /**
     *
     * Send {@code HttpMethod.GET} request and get response from an external API as the ResponseEntity.
     *
     * @param uri URI of the request to the API. Can't be {@code null}.
     * @throws APICallException when nothing has been found or there is client or server error.
     * @return response from API as an entity.
     */
    @Override
    public ResponseEntity<Item> requestData(@NonNull URI uri) throws APICallException {
        ResponseEntity<Item> response = restTemplate.getForEntity(uri, Item.class);
        log.debug("Response taken from external API. Status code = {}; URI = {}; Body = {}", response.getStatusCode(), uri, response.getBody());
        return response;
    }
}
