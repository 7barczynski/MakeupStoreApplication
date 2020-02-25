package com.tbar.makeupstoreapplication.utility.exceptions.servicelayer;

import java.util.Map;

public class APIConnectionException extends ServiceLayerException {

    // === constants ===
    private static final String MESSAGE = "External API respond with error status code.";

    // === constructors ===
    public APIConnectionException(String requestUri, Map<String, String> parameters) {
        super(MESSAGE + " Request URI = " + requestUri + ", parameters = " + parameters);
    }

    public APIConnectionException(String requestUri, Long id) {
        super(MESSAGE + " Request URI = " + requestUri + ", ID = " + id);
    }
}
