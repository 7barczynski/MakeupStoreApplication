package com.tbar.makeupstoreapplication.utility.exceptions.servicelayer;

import java.util.Map;

public class ProductNotFoundException extends ServiceLayerException {

    // === constants ===
    private static final String MESSAGE = "Product cannot be found.";

    // === constructors ===
    public ProductNotFoundException(String requestUri, Map<String, String> parameters) {
        super(MESSAGE + " Request URI = " + requestUri + ", parameters = " + parameters);
    }

    public ProductNotFoundException(String requestUri, Long id) {
        super(MESSAGE + " Request URI = " + requestUri + ", ID = " + id);
    }
}
