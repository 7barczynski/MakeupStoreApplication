package com.tbar.MakeupStoreApplication.utility.exceptions;

public class APIConnectionException extends Exception {
    // === constants ===
    private static final String MESSAGE = "External API respond with status code different from OK (200).";

    // === constructors ===
    public APIConnectionException() {
        super(MESSAGE);
    }

    public APIConnectionException(int statusCode) {
        super(MESSAGE + " Status code = " + statusCode);
    }

    public APIConnectionException(String requestString, int statusCode) {
        super(MESSAGE + " Status code = " + statusCode + ". Request string = " + requestString);
    }

}
