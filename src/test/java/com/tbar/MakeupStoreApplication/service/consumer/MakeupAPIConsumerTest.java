package com.tbar.MakeupStoreApplication.service.consumer;

import com.tbar.MakeupStoreApplication.service.consumer.model.Item;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MakeupAPIConsumerTest {

    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private MakeupAPIConsumer apiConsumer;
    private ResponseEntity<List<Item>> expectedResponse;
    private ResponseEntity<List<Item>> serviceUnavailableResponse;
    private ResponseEntity<List<Item>> internalServerErrorResponse;
    private ResponseEntity<List<Item>> actualResponse;
    private final URI EXAMPLE_URI = URI.create("http://www.example.com");

    @BeforeEach
    void set_up() {
        expectedResponse = new ResponseEntity<>(new ArrayList<>(List.of(new Item())), HttpStatus.OK);
        serviceUnavailableResponse = new ResponseEntity<>(null, HttpStatus.SERVICE_UNAVAILABLE);
        internalServerErrorResponse = new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void given_URI_when_requestData_return_ResponseEntity() {
        when(restTemplate.exchange(
                eq(EXAMPLE_URI),
                eq(HttpMethod.GET),
                isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<List<Item>>>any()
        )).thenReturn(expectedResponse);

        actualResponse = apiConsumer.requestData(EXAMPLE_URI);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void given_nullURI_when_requestData_return_emptyResponseEntityWith500StatusCode() {
        actualResponse = apiConsumer.requestData(null);

        assertEquals(internalServerErrorResponse, actualResponse);
    }

    @Test
    void given_RestClientException_when_requestData_return_ResponseEntityWith503StatusCode() {
        when(restTemplate.exchange(
                eq(EXAMPLE_URI),
                eq(HttpMethod.GET),
                isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<List<Item>>>any()
        )).thenThrow(RestClientException.class);

        actualResponse = apiConsumer.requestData(EXAMPLE_URI);

        assertEquals(serviceUnavailableResponse, actualResponse);
    }

}
