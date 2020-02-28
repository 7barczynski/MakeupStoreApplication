package com.tbar.makeupstoreapplication.service.consumer;

import com.tbar.makeupstoreapplication.service.consumer.model.Item;
import com.tbar.makeupstoreapplication.utility.errorhandlers.MakeupAPIErrorHandler;
import com.tbar.makeupstoreapplication.utility.exceptions.APICallException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;
    private MockRestServiceServer mockRestServiceServer;
    private RestTemplate restTemplate;
    private MakeupAPIConsumer makeupAPIConsumer;
    private Item expectedSingleObjectResponse;
    private List<Item> expectedCollectionResponse;
    private final Item expectedItem = new Item();
    private final URI exampleUri = URI.create("www.example.com");
    private final Long exampleId = 1000L;
    private final String expectedSingleObjectJsonResponse = "{\"id\" : \"" + exampleId + "\"}";
    private final String expectedCollectionJsonResponse = "[{\"id\" : \"" + exampleId + "\"}]";

    @BeforeEach
    void init() {
        restTemplate = restTemplateBuilder.errorHandler(new MakeupAPIErrorHandler()).build();
        mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);
        makeupAPIConsumer = new MakeupAPIConsumer(restTemplate);
        expectedItem.setId(exampleId);
        expectedSingleObjectResponse = expectedItem;
        expectedCollectionResponse = new ArrayList<>(List.of(expectedItem));
    }

    @Test
    void given_successfulApiCall_when_requestSingleObject_return_item() throws APICallException {
        setupSuccessfulMockServerResponse(expectedSingleObjectJsonResponse);
        Item actualResponse = makeupAPIConsumer.requestSingleObject(exampleUri);
        assertEquals(expectedSingleObjectResponse, actualResponse);
        mockRestServiceServer.verify();
    }

    @Test
    void given_successfulApiCall_when_requestCollection_return_collectionOfItems() throws APICallException {
        setupSuccessfulMockServerResponse(expectedCollectionJsonResponse);
        List<Item> actualResponse = makeupAPIConsumer.requestCollection(exampleUri);
        assertEquals(expectedCollectionResponse, actualResponse);
        mockRestServiceServer.verify();
    }

    @Test
    void given_apiCallServerError_when_requestSingleObject_throw_APICallException() {
        setupErrorMockServerResponse(HttpStatus.SERVICE_UNAVAILABLE);
        assertThrows(APICallException.class, ()-> makeupAPIConsumer.requestSingleObject(exampleUri));
        mockRestServiceServer.verify();
    }

    @Test
    void given_apiCallServerError_when_requestCollection_throw_APICallException() {
        setupErrorMockServerResponse(HttpStatus.SERVICE_UNAVAILABLE);
        assertThrows(APICallException.class, ()-> makeupAPIConsumer.requestCollection(exampleUri));
        mockRestServiceServer.verify();
    }

    @Test
    void given_apiCallClientError_when_requestSingleObject_throw_APICallException() {
        setupErrorMockServerResponse(HttpStatus.BAD_REQUEST);
        assertThrows(APICallException.class, ()-> makeupAPIConsumer.requestSingleObject(exampleUri));
        mockRestServiceServer.verify();
    }

    @Test
    void given_apiCallClientError_when_requestCollection_throw_APICallException() {
        setupErrorMockServerResponse(HttpStatus.BAD_REQUEST);
        assertThrows(APICallException.class, ()-> makeupAPIConsumer.requestCollection(exampleUri));
        mockRestServiceServer.verify();
    }

    private void setupSuccessfulMockServerResponse(String responseBody) {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(exampleUri))
                .andExpect(method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess(responseBody, MediaType.APPLICATION_JSON));
    }

    private void setupErrorMockServerResponse(HttpStatus httpStatus) {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(exampleUri))
                .andExpect(method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withStatus(httpStatus));
    }
}
