package com.tbar.makeupstoreapplication.errorHandlers;

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
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RestClientTest
class MakeupAPIErrorHandlerTest {

    // === constants ===
    private final String EXAMPLE_URI = "http://www.example.com";

    // === fields ===
    @Autowired
    private RestTemplateBuilder restTemplateBuilder;
    private MockRestServiceServer mockRestServiceServer;
    private RestTemplate restTemplate;

    // === initialization ===
    @BeforeEach
    void init() {
        restTemplate = restTemplateBuilder.errorHandler(new MakeupAPIErrorHandler()).build();
        mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);
    }

    // === tests ===
    @Test
    void given_httpRequest_when_serverSideError_throw_APICallServerSideException() {
        this.mockRestServiceServer.expect(ExpectedCount.once(), requestTo(EXAMPLE_URI))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));

        assertThrows(APICallServerSideException.class,() -> restTemplate.getForEntity(EXAMPLE_URI, null));
        this.mockRestServiceServer.verify();
    }

    @Test
    void given_httpRequest_when_404Error_throw_ProductNotFoundException() {
        this.mockRestServiceServer.expect(ExpectedCount.once(), requestTo(EXAMPLE_URI))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThrows(APICallNotFoundException.class,() -> restTemplate.getForEntity(EXAMPLE_URI, null));
        this.mockRestServiceServer.verify();
    }

    @Test
    void given_httpRequest_when_clientSideError_throw_APICallClientSideException() {
        this.mockRestServiceServer.expect(ExpectedCount.once(), requestTo(EXAMPLE_URI))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        assertThrows(APICallClientSideException.class,() -> restTemplate.getForEntity(EXAMPLE_URI, null));
        this.mockRestServiceServer.verify();
    }

}
