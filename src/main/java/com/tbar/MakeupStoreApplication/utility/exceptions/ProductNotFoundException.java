package com.tbar.MakeupStoreApplication.utility.exceptions;

public class ProductNotFoundException extends Exception {

    // === constructors ===
    public ProductNotFoundException() {
        super("Product cannot be found in external API.");
    }

    public ProductNotFoundException(String requestArgument) {
        super("Product cannot be found in external API. Request argument = " + requestArgument);
    }

}
