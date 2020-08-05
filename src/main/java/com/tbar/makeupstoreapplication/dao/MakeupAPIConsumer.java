package com.tbar.makeupstoreapplication.dao;

import com.tbar.makeupstoreapplication.model.Product;
import com.tbar.makeupstoreapplication.utility.AppProperties;
import com.tbar.makeupstoreapplication.utility.exceptions.APIConnectionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

@Slf4j
@Component
public class MakeupAPIConsumer implements APIConsumer<Product> {

    private final RestTemplate restTemplate;
    private final URI makeupApiUriForAllProducts;

    public MakeupAPIConsumer(RestTemplate restTemplate, AppProperties appProperties) {
        this.restTemplate = restTemplate;
        this.makeupApiUriForAllProducts = URI.create(appProperties.getMakeupApiUriForAllProducts());
    }

    @Override
    public List<Product> requestCollection() throws APIConnectionException {
        ResponseEntity<List<Product>> response = restTemplate.exchange(makeupApiUriForAllProducts, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {});
        log.debug("Response taken from external API. Status code = {}; URI = {}; Size = {}", response.getStatusCode(),
                makeupApiUriForAllProducts, response.getBody() != null ? response.getBody().size() : "0");
        return response.getBody();
    }
}
