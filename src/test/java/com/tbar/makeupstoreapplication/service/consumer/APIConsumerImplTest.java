package com.tbar.makeupstoreapplication.service.consumer;

import com.tbar.makeupstoreapplication.service.consumer.model.Item;
import com.tbar.makeupstoreapplication.utility.exceptions.APIConnectionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
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
    @InjectMocks
    private MakeupAPIConsumer makeupAPIConsumer;
    private final List<Item> expectedCollectionResponse = new ArrayList<>(List.of(new Item()));
    private final Item expectedSingleObjectResponse = new Item();
    private final URI exampleUrl = URI.create("http://www.example.com");

    @Test
    void given_uri_when_requestCollection_return_collectionOfItems() throws APIConnectionException {
        setupMockRestTemplateResponseForCollection();
        List<Item> actualResponse = makeupAPIConsumer.requestCollection(exampleUrl);
        assertEquals(expectedCollectionResponse, actualResponse);
    }

    @Test
    void given_uri_when_requestSingleObject_return_item() throws APIConnectionException {
        setupMockRestTemplateResponseForSingleObject();
        Item actualResponse = makeupAPIConsumer.requestSingleObject(exampleUrl);
        assertEquals(expectedSingleObjectResponse, actualResponse);
    }

    private void setupMockRestTemplateResponseForCollection() {
        when(restTemplate.exchange(
                eq(exampleUrl),
                eq(HttpMethod.GET),
                isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<List<Item>>>any()
        )).thenReturn(new ResponseEntity<>(expectedCollectionResponse, HttpStatus.OK));
    }

    private void setupMockRestTemplateResponseForSingleObject() {
        when(restTemplate.getForEntity(eq(exampleUrl), eq(Item.class)))
                .thenReturn(new ResponseEntity<>(expectedSingleObjectResponse, HttpStatus.OK));
    }
}
