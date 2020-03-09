package com.tbar.makeupstoreapplication.utility.errorhandlers;

import com.tbar.makeupstoreapplication.utility.exceptions.APIConnectionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Scanner;

@Slf4j
@Component
public class MakeupAPIErrorHandler implements ResponseErrorHandler {
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().isError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        String responseAsString = toString(response.getStatusCode(), response.getBody());
        log.error("Response status and body = {}", responseAsString);
        throw new APIConnectionException(responseAsString);
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        String responseAsString = toString(response.getStatusCode(), response.getBody());
        log.error("URL = {}, HttpMetthod = {}, Response status and body = {}", url, method, responseAsString);
        throw new APIConnectionException(url, method, responseAsString);
    }

    private String toString(HttpStatus httpStatus, InputStream responseBody) {
        String statusCode = httpStatus.toString();
        Scanner scanner = new Scanner(responseBody).useDelimiter("\\A");
        return statusCode + "; " + (scanner.hasNext() ? scanner.next() : "");
    }
}
