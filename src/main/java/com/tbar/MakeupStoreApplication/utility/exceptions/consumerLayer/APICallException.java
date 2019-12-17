package com.tbar.MakeupStoreApplication.utility.exceptions.consumerLayer;

/**
 * This is a wrapper exception of all errors caused by calling external API.
 *
 * @author 7oamsz8
 */
public class APICallException extends RuntimeException {

    // === constructors ===
    APICallException(String message) {
        super(message);
    }

}
