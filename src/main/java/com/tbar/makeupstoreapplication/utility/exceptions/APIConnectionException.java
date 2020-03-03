package com.tbar.makeupstoreapplication.utility.exceptions;

import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientException;

import java.net.URI;

public class APIConnectionException extends RestClientException {

    private static final String MESSAGE = "Error at calling API.";

    public APIConnectionException(String responseBody) {
        super(String.format("%s Response body = %s", MESSAGE, responseBody));
    }

    public APIConnectionException(URI url, HttpMethod method, String responseBody) {
        super(String.format("%s URI = %s, HttpMethod = %s, Response body = %s", MESSAGE, url, method, responseBody));
    }
}
