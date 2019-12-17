package com.tbar.MakeupStoreApplication.utility.exceptions.consumerLayer;

public class APICallNotFoundException extends APICallException {

    // === constants ===
    private static final String MESSAGE = "Resource cannot be found.";

    // === constructors ===
    public APICallNotFoundException() {
        super(MESSAGE);
    }
}
