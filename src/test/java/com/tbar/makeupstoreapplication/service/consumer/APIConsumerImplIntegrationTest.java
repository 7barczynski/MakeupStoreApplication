package com.tbar.makeupstoreapplication.service.consumer;

import com.tbar.makeupstoreapplication.dao.MakeupAPIConsumer;
import com.tbar.makeupstoreapplication.model.Product;
import com.tbar.makeupstoreapplication.utility.AppProperties;
import com.tbar.makeupstoreapplication.utility.errorhandlers.MakeupAPIErrorHandler;
import com.tbar.makeupstoreapplication.utility.exceptions.APIConnectionException;
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
    private MakeupAPIConsumer makeupAPIConsumer;
    private Product product = new Product();
    private List<Product> expectedResponse = new ArrayList<>(List.of(product));
    private URI exampleUri = URI.create("www.example.com");
    private Long exampleId = 1000L;
    private String expectedCollectionJsonResponse = "[{\"id\" : \"" + exampleId + "\"}]";
    private AppProperties appProperties = new AppProperties();

    @BeforeEach
    void init() {
        RestTemplate restTemplate = restTemplateBuilder.errorHandler(new MakeupAPIErrorHandler()).build();
        mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);
        appProperties.setMakeupApiUriForAllProducts(exampleUri.toString());
        makeupAPIConsumer = new MakeupAPIConsumer(restTemplate, appProperties);
        product.setId(exampleId);
    }

    @Test
    void given_successfulApiCall_when_requestCollection_return_collectionOfProducts() {
        setupSuccessfulMockServerResponse(expectedCollectionJsonResponse);
        List<Product> actualResponse = makeupAPIConsumer.requestCollection();
        assertEquals(expectedResponse, actualResponse);
        mockRestServiceServer.verify();
    }

    @Test
    void given_apiCallServerError_when_requestCollection_throw_APIConnectionException() {
        setupErrorMockServerResponse(HttpStatus.SERVICE_UNAVAILABLE);
        assertThrows(APIConnectionException.class, ()-> makeupAPIConsumer.requestCollection());
        mockRestServiceServer.verify();
    }

    @Test
    void given_apiCallClientError_when_requestCollection_throw_APIConnectionException() {
        setupErrorMockServerResponse(HttpStatus.BAD_REQUEST);
        assertThrows(APIConnectionException.class, ()-> makeupAPIConsumer.requestCollection());
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
