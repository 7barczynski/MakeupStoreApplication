package com.tbar.makeupstoreapplication.dao;

import com.tbar.makeupstoreapplication.model.Product;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

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
    private APIConsumer<Product> makeupAPIConsumer;
    private final String exampleUri = "http://www.example.com";

    @BeforeEach
    void init() {
        makeupAPIConsumer = new MakeupAPIConsumer(restTemplate);
        ReflectionTestUtils.setField(makeupAPIConsumer, "makeupApiUriForAllProducts", exampleUri);
    }

    @Test
    void when_requestCollection_then_returnCollectionOfProducts() {
        List<Product> expectedResponse = createListForResponse();
        setupMockRestTemplateResponseForCollection(expectedResponse);

        List<Product> actualResponse = makeupAPIConsumer.requestCollection();
        assertEquals(expectedResponse, actualResponse);
    }

    private ArrayList<Product> createListForResponse() {
        return new ArrayList<>(Collections.nCopies(100, new Product()));
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
