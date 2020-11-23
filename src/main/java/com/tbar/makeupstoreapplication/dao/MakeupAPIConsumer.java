package com.tbar.makeupstoreapplication.dao;

import com.tbar.makeupstoreapplication.model.Product;
import com.tbar.makeupstoreapplication.utility.exceptions.APIConnectionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MakeupAPIConsumer implements APIConsumer<Product> {

    private final RestTemplate restTemplate;
    @Value("${makeupApiUriForAllProducts}")
    private String makeupApiUriForAllProducts;

    @Override
    public List<Product> requestCollection() throws APIConnectionException {
        ResponseEntity<List<Product>> response = restTemplate.exchange(makeupApiUriForAllProducts, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {});
        log.debug("Response taken from external API. Status code = {}; URI = {}; Size = {}", response.getStatusCode(),
                URI.create(makeupApiUriForAllProducts), response.getBody() != null ? response.getBody().size() : "0");
        return response.getBody();
    }
}
