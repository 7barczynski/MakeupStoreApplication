package com.tbar.makeupstoreapplication.utility.exceptions;

import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientException;

import java.net.URI;

public class APICallException extends RestClientException {

    private static final String MESSAGE = "Error at calling API.";

    public APICallException(String responseBody) {
        super(String.format("%s Response body = %s", MESSAGE, responseBody));
    }

    public APICallException(URI url, HttpMethod method, String responseBody) {
        super(String.format("%s URI = %s, HttpMethod = %s, Response body = %s", MESSAGE, url, method, responseBody));
    }
}
