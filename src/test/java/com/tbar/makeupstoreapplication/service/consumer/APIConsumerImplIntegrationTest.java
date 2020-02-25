package com.tbar.makeupstoreapplication.service.consumer;

import com.tbar.makeupstoreapplication.service.consumer.model.Item;
import com.tbar.makeupstoreapplication.utility.exceptions.consumerlayer.APICallClientSideException;
import com.tbar.makeupstoreapplication.utility.exceptions.consumerlayer.APICallServerSideException;
import com.tbar.makeupstoreapplication.utility.exceptions.consumerlayer.APICallNotFoundException;
import com.tbar.makeupstoreapplication.utility.errorhandlers.MakeupAPIErrorHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

@RestClientTest
class APIConsumerImplIntegrationTest {


    // === constants ===
    private final String EXAMPLE_URI = "www.example.com";
    private final String EXPECTED_SOLO_JSON_RESPONSE = "{\"id\" : \"1000\"}";
    private final String EXPECTED_MULTI_JSON_RESPONSE = "[{\"id\" : \"1000\"}]";


    // === fields ===
    @Autowired
    private RestTemplateBuilder restTemplateBuilder;
    private MockRestServiceServer mockRestServiceServer;
    private RestTemplate restTemplate;
    private SoloAPIConsumer soloAPIConsumer;
    private MultiAPIConsumer multiAPIConsumer;
    private ResponseEntity<Item> expectedSoloResponse;
    private ResponseEntity<List<Item>> expectedMultiResponse;
    private Item expectedItem = new Item();

    // === initialization ===
    @BeforeEach
    void init() {
        restTemplate = restTemplateBuilder.errorHandler(new MakeupAPIErrorHandler()).build();
        mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);
        soloAPIConsumer = new SoloAPIConsumer(restTemplate);
        multiAPIConsumer = new MultiAPIConsumer(restTemplate);
        expectedItem.setId(1000L);
        expectedSoloResponse = new ResponseEntity<>(expectedItem, HttpStatus.OK);
        expectedMultiResponse = new ResponseEntity<>(new ArrayList<>(List.of(expectedItem)), HttpStatus.OK);
    }

    // === tests ===
    @Test
    void given_apiCall_when_soloAPIConsumerRequestData_return_ResponseEntity() {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(EXAMPLE_URI))
                .andExpect(method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess(EXPECTED_SOLO_JSON_RESPONSE, MediaType.APPLICATION_JSON));

        ResponseEntity<Item> actualResponse = soloAPIConsumer.requestData(URI.create(EXAMPLE_URI));

        assertEquals(expectedSoloResponse.getBody(), actualResponse.getBody());
        assertEquals(expectedSoloResponse.getStatusCode(), actualResponse.getStatusCode());
    }

    @Test
    void given_apiCall_when_multiAPIConsumerRequestData_return_ResponseEntity() {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(EXAMPLE_URI))
                .andExpect(method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess(EXPECTED_MULTI_JSON_RESPONSE, MediaType.APPLICATION_JSON));

        ResponseEntity<List<Item>> actualResponse = multiAPIConsumer.requestData(URI.create(EXAMPLE_URI));

        assertEquals(expectedMultiResponse.getBody(), actualResponse.getBody());
        assertEquals(expectedMultiResponse.getStatusCode(), actualResponse.getStatusCode());
    }

    @Test
    void given_apiCallServerError_when_soloConsumer_throw_APICallServerSideException() {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(EXAMPLE_URI))
                .andExpect(method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.SERVICE_UNAVAILABLE));

        assertThrows(APICallServerSideException.class, ()-> soloAPIConsumer.requestData(URI.create(EXAMPLE_URI)));
    }

    @Test
    void given_apiCallServerError_when_multiConsumer_throw_APICallServerSideException() {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(EXAMPLE_URI))
                .andExpect(method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.SERVICE_UNAVAILABLE));

        assertThrows(APICallServerSideException.class, ()-> multiAPIConsumer.requestData(URI.create(EXAMPLE_URI)));
    }

    @Test
    void given_apiCallClientError_when_soloConsumer_throw_APICallClientSideException() {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(EXAMPLE_URI))
                .andExpect(method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.BAD_REQUEST));

        assertThrows(APICallClientSideException.class, ()-> soloAPIConsumer.requestData(URI.create(EXAMPLE_URI)));
    }

    @Test
    void given_apiCallClientError_when_multiConsumer_throw_APICallClientSideException() {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(EXAMPLE_URI))
                .andExpect(method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.BAD_REQUEST));

        assertThrows(APICallClientSideException.class, ()-> multiAPIConsumer.requestData(URI.create(EXAMPLE_URI)));
    }

    @Test
    void given_apiCallNotFoundError_when_soloConsumer_throw_ProductNotFoundException() {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(EXAMPLE_URI))
                .andExpect(method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.NOT_FOUND));

        assertThrows(APICallNotFoundException.class, ()-> soloAPIConsumer.requestData(URI.create(EXAMPLE_URI)));
    }

    @Test
    void given_apiCallNotFoundError_when_multiConsumer_throw_ProductNotFoundException() {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(EXAMPLE_URI))
                .andExpect(method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.NOT_FOUND));

        assertThrows(APICallNotFoundException.class, ()-> multiAPIConsumer.requestData(URI.create(EXAMPLE_URI)));
    }
}
