package com.tbar.makeupstoreapplication.utility.exceptions;

public class SingleProductNotFoundException extends Exception {

    private static final String MESSAGE = "Single product cannot be found in external API.";

    public SingleProductNotFoundException() {
        super(MESSAGE);
    }

    public SingleProductNotFoundException(String requestUri, Long id) {
        super(MESSAGE + " Request URI = " + requestUri + ", ID = " + id);
    }
}
