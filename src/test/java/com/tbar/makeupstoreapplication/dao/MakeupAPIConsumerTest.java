package com.tbar.makeupstoreapplication.dao;

import com.tbar.makeupstoreapplication.model.Product;
import com.tbar.makeupstoreapplication.utility.AppProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MakeupAPIConsumerTest {

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private AppProperties appProperties;
    private APIConsumer<Product> makeupAPIConsumer;
    private final URI exampleUri = URI.create("http://www.example.com");

    @BeforeEach
    void init() {
        when(appProperties.getMakeupApiUriForAllProducts()).thenReturn(exampleUri.toString());
        makeupAPIConsumer = new MakeupAPIConsumer(restTemplate, appProperties);
    }

    @Test
    void when_requestCollection_return_collectionOfProducts() {
        List<Product> expectedResponse = new ArrayList<>(Collections.nCopies(100, new Product()));
        setupMockRestTemplateResponseForCollection(expectedResponse);

        List<Product> actualResponse = makeupAPIConsumer.requestCollection();
        assertEquals(expectedResponse, actualResponse);
    }

    private void setupMockRestTemplateResponseForCollection(List<Product> listToResponse) {
        when(restTemplate.exchange(
                eq(exampleUri),
                eq(HttpMethod.GET),
                isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<List<Product>>>any()
        )).thenReturn(new ResponseEntity<>(listToResponse, HttpStatus.OK));
    }
}
