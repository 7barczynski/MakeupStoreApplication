package com.tbar.makeupstoreapplication.service.consumer;

import com.tbar.makeupstoreapplication.service.consumer.model.Product;
import com.tbar.makeupstoreapplication.utility.exceptions.APIConnectionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

@Slf4j
@Component
public class MakeupAPIConsumer implements APIConsumer<Product> {

    private RestTemplate restTemplate;

    @Autowired
    public MakeupAPIConsumer(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
     public Product requestSingleObject(@NonNull URI uri) throws APIConnectionException {
        ResponseEntity<Product> response = restTemplate.getForEntity(uri, Product.class);
        if (response.getBody() == null) {
            return null;
        }
        logResponse(response.getStatusCode(), uri, response.getBody());
        return response.getBody();
    }

    @Override
    public List<Product> requestCollection(@NonNull URI uri) throws APIConnectionException {
        ResponseEntity<List<Product>> response;
        response = restTemplate.exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
        logResponse(response.getStatusCode(), uri, response.getBody());
        return response.getBody();
    }

    private void logResponse(HttpStatus statusCode, URI uri, Object body) {
        log.debug("Response taken from external API. Status code = {}; URI = {}; Body = {}",
                statusCode, uri, body);
    }
}