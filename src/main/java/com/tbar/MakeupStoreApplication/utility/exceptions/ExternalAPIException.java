package com.tbar.MakeupStoreApplication.utility.exceptions;

public class ExternalAPIException extends Exception {
    // === constants ===
    private static final String MESSAGE = "External API respond with status code different from OK (200).";

    // === constructors ===
    public ExternalAPIException() {
        super(MESSAGE);
    }

    public ExternalAPIException(int statusCode) {
        super(MESSAGE + " Status code = " + statusCode);
    }

    public ExternalAPIException(String requestString, int statusCode) {
        super(MESSAGE + " Status code = " + statusCode + ". Request string = " + requestString);
    }

}
