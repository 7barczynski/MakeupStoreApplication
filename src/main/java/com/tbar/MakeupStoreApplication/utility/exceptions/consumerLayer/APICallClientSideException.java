package com.tbar.MakeupStoreApplication.utility.exceptions.consumerLayer;

public class APICallClientSideException extends APICallException {
    // === constants ===
    private static final String MESSAGE = "API client side exception.";

    // === constructors ===
    public APICallClientSideException(int statusCode) {
        super(MESSAGE + " Status code = " + statusCode);
    }
}
