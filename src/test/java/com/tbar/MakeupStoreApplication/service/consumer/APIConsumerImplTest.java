package com.tbar.MakeupStoreApplication.service.consumer;

import com.tbar.MakeupStoreApplication.service.consumer.model.Item;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class APIConsumerImplTest {

    // === constants ===
    private final ResponseEntity<Object> EXPECTED_RESPONSE = new ResponseEntity<>(new Object(), HttpStatus.OK);
    private final ResponseEntity<Object> EXPECTED_SERVICE_UNAVAILABLE_RESPONSE = new ResponseEntity<>(null, HttpStatus.SERVICE_UNAVAILABLE);
    private final URI EXAMPLE_URI = URI.create("http://www.example.com");
    // === fields ===
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private APIConsumerImpl<Object> apiConsumer;

    // === tests ===
    @Test
    void given_Uri_when_requestData_return_ResponseEntity() {
        when(restTemplate.exchange(
                eq(EXAMPLE_URI),
                eq(HttpMethod.GET),
                isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<Object>>any()
        )).thenReturn(EXPECTED_RESPONSE);

        ResponseEntity<Object> actualResponse = apiConsumer.requestData(EXAMPLE_URI);

        assertEquals(EXPECTED_RESPONSE, actualResponse);
    }

    @Test
    void given_RestClientException_when_requestData_return_ResponseEntityWith503StatusCode() {
        when(restTemplate.exchange(
                eq(EXAMPLE_URI),
                eq(HttpMethod.GET),
                isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<List<Item>>>any()
        )).thenThrow(RestClientException.class);

        ResponseEntity<Object> actualResponse = apiConsumer.requestData(EXAMPLE_URI);

        assertEquals(EXPECTED_SERVICE_UNAVAILABLE_RESPONSE, actualResponse);
    }

}
