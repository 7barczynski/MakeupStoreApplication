package com.tbar.makeupstoreapplication.utility.exceptions;

import java.util.Map;

public class ProductNotFoundException extends Exception {

    private static final String MESSAGE = "Product cannot be found in external API.";

    public ProductNotFoundException() {
        super(MESSAGE);
    }

    public ProductNotFoundException(String requestUri, Map<String, String> parameters) {
        super(MESSAGE + " Request URI = " + requestUri + ", parameters = " + parameters);
    }

    public ProductNotFoundException(String requestUri, Long id) {
        super(MESSAGE + " Request URI = " + requestUri + ", ID = " + id);
    }
}
