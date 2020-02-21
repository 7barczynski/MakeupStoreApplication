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
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductConsumerImplTest {

    // === constants ===
    private final ResponseEntity<List<Item>> EXPECTED_RESPONSE_FOR_MULTI = new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
    private final ResponseEntity<Item> EXPECTED_RESPONSE_FOR_SOLO = new ResponseEntity<>(new Item(), HttpStatus.OK);
    private final URI EXAMPLE_URI = URI.create("http://www.example.com");
    // === fields ===
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private ProductProductConsumer productAPIConsumer;
    @InjectMocks
    private SoloAPIConsumer soloAPIConsumer;

    // === tests ===
    @Test
    void given_uri_when_requestDataInMultiAPIConsumer_return_ResponseEntity() {
        when(restTemplate.exchange(
                eq(EXAMPLE_URI),
                eq(HttpMethod.GET),
                isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<List<Item>>>any()
        )).thenReturn(EXPECTED_RESPONSE_FOR_MULTI);

        ResponseEntity<List<Item>> actualResponse = productAPIConsumer.requestData(EXAMPLE_URI);

        assertEquals(EXPECTED_RESPONSE_FOR_MULTI, actualResponse);
    }

    @Test
    void given_uri_when_requestDataInSoloAPIConsumer_return_ResponseEntity() {
        when(restTemplate.getForEntity(eq(EXAMPLE_URI), eq(Item.class))).thenReturn(EXPECTED_RESPONSE_FOR_SOLO);

        ResponseEntity<Item> actualResponse = soloAPIConsumer.requestData(EXAMPLE_URI);

        assertEquals(EXPECTED_RESPONSE_FOR_SOLO, actualResponse);
    }
}
