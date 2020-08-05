package com.tbar.makeupstoreapplication.errorHandlers;

import com.tbar.makeupstoreapplication.model.Product;
import com.tbar.makeupstoreapplication.utility.errorhandlers.MakeupAPIErrorHandler;
import com.tbar.makeupstoreapplication.utility.exceptions.APIConnectionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RestClientTest
class MakeupAPIErrorHandlerTest {

    private MockRestServiceServer mockRestServiceServer;
    private RestTemplate restTemplate;
    private final String exampleUrl = "http://www.example.com";

    @BeforeEach
    void init() {
        restTemplate = new RestTemplateBuilder().errorHandler(new MakeupAPIErrorHandler()).build();
        mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void given_httpRequest_when_serverSideError_throw_APICallException() {
        setupMockServerResponse(HttpStatus.SERVICE_UNAVAILABLE);
        assertThrows(APIConnectionException.class,() -> restTemplate.getForEntity(exampleUrl, Product.class));
        mockRestServiceServer.verify();
    }

    @Test
    void given_httpRequest_when_clientSideError_throw_APICallException() {
        setupMockServerResponse(HttpStatus.BAD_REQUEST);
        assertThrows(APIConnectionException.class,() -> restTemplate.getForEntity(exampleUrl, Product.class));
        mockRestServiceServer.verify();
    }

    private void setupMockServerResponse(HttpStatus httpStatus) {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(exampleUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(httpStatus));
    }
}
