package com.tbar.makeupstoreapplication.utility.exceptions.consumerlayer;

public class APICallNotFoundException extends APICallException {

    // === constants ===
    private static final String MESSAGE = "Resource cannot be found.";

    // === constructors ===
    public APICallNotFoundException() {
        super(MESSAGE);
    }
}
