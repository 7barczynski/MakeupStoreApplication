package com.tbar.MakeupStoreApplication.utility.exceptions;

public class ProductNotFoundException extends APICallException {

    // === constants ===
    private static final String MESSAGE = "Product cannot be found in external API.";

    // === constructors ===
    public ProductNotFoundException() {
        super(MESSAGE);
    }

    public ProductNotFoundException(String requestUri) {
        super(MESSAGE + " Request URI = " + requestUri);
    }

}
