package com.tbar.makeupstoreapplication.utility.exceptions.consumerlayer;

public class APICallServerSideException extends APICallException {
    // === constants ===
    private static final String MESSAGE = "API server side exception.";

    public APICallServerSideException(int statusCode) {
        super(MESSAGE + " Status code = " + statusCode);
    }
}
