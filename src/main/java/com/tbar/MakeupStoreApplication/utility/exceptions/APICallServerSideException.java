package com.tbar.MakeupStoreApplication.utility.exceptions;

public class APICallServerSideException extends APICallException {
    // === constants ===
    private static final String MESSAGE = "API server side exception.";

    public APICallServerSideException(int statusCode) {
        super(MESSAGE + " Status code = " + statusCode);
    }
}
