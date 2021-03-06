package com.tbar.makeupstoreapplication.dao;

import com.tbar.makeupstoreapplication.model.Product;
import com.tbar.makeupstoreapplication.utility.errorhandlers.MakeupAPIErrorHandler;
import com.tbar.makeupstoreapplication.utility.exceptions.APIConnectionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
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
class MakeupAPIConsumerIntegrationTest {

    private MockRestServiceServer mockRestServiceServer;
    private MakeupAPIConsumer makeupAPIConsumer;
    private final URI exampleUri = URI.create("/example");

    @BeforeEach
    void init() {
        RestTemplate restTemplate = new RestTemplateBuilder().errorHandler(new MakeupAPIErrorHandler()).build();
        mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);
        makeupAPIConsumer = new MakeupAPIConsumer(restTemplate);
        ReflectionTestUtils.setField(makeupAPIConsumer, "makeupApiUriForAllProducts", exampleUri.toString());
    }

    @Test
    void given_successfulApiCall_when_requestCollection_then_returnCollectionOfProducts() {
        long exampleId = 1000L;
        List<Product> expectedResponse = createListForResponse(exampleId);
        String expectedJsonResponse = createJsonResponse(exampleId);


        setupSuccessfulMockServerResponse(expectedJsonResponse);
        List<Product> actualResponse = makeupAPIConsumer.requestCollection();
        assertEquals(expectedResponse, actualResponse);
        mockRestServiceServer.verify();
    }

    @Test
    void given_apiCallServerError_when_requestCollection_then_throwAPIConnectionException() {
        setupErrorMockServerResponse(HttpStatus.SERVICE_UNAVAILABLE);
        assertThrows(APIConnectionException.class, () -> makeupAPIConsumer.requestCollection());
        mockRestServiceServer.verify();
    }

    @Test
    void given_apiCallClientError_when_requestCollection_then_throwAPIConnectionException() {
        setupErrorMockServerResponse(HttpStatus.BAD_REQUEST);
        assertThrows(APIConnectionException.class, () -> makeupAPIConsumer.requestCollection());
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

    private ArrayList<Product> createListForResponse(long exampleId) {
        Product exampleProduct = createProductWithId(exampleId);
        return new ArrayList<>(List.of(exampleProduct));
    }

    private Product createProductWithId(long exampleId) {
        Product product = new Product();
        product.setId(exampleId);
        return product;
    }

    private String createJsonResponse(long exampleId) {
        return "[{\"id\" : \"" + exampleId + "\"}]";
    }
}
