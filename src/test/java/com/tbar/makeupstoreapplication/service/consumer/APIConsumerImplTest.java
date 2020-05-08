package com.tbar.makeupstoreapplication.service.consumer;

import com.tbar.makeupstoreapplication.dao.APIConsumer;
import com.tbar.makeupstoreapplication.dao.MakeupAPIConsumer;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class APIConsumerImplTest {

    @Mock
    private RestTemplate restTemplate;
    private APIConsumer<Product> makeupAPIConsumer;
    private List<Product> expectedCollectionResponse = new ArrayList<>(List.of(new Product()));
    private URI exampleUri = URI.create("http://www.example.com");
    private AppProperties appProperties = new AppProperties();

    @BeforeEach
    void init() {
        appProperties.setMakeupApiUriForAllProducts(exampleUri.toString());
        makeupAPIConsumer = new MakeupAPIConsumer(restTemplate, appProperties);
    }

    @Test
    void given_uri_when_requestCollection_return_collectionOfProducts() {
        setupMockRestTemplateResponseForCollection();
        List<Product> actualResponse = makeupAPIConsumer.requestCollection();
        assertEquals(expectedCollectionResponse, actualResponse);
    }

    private void setupMockRestTemplateResponseForCollection() {
        when(restTemplate.exchange(
                eq(exampleUri),
                eq(HttpMethod.GET),
                isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<List<Product>>>any()
        )).thenReturn(new ResponseEntity<>(expectedCollectionResponse, HttpStatus.OK));
    }
}
