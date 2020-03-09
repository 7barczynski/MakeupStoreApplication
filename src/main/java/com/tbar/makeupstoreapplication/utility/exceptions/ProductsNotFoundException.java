package com.tbar.makeupstoreapplication.utility.exceptions;

import java.util.Map;

public class ProductsNotFoundException extends Exception {

    private static final String MESSAGE = "Product cannot be found in external API.";

    public ProductsNotFoundException() {
        super(MESSAGE);
    }

    public ProductsNotFoundException(String requestUri, Map<String, String> parameters) {
        super(MESSAGE + " Request URI = " + requestUri + ", parameters = " + parameters);
    }
}
