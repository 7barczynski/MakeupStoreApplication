package com.tbar.makeupstoreapplication.utility.exceptions.servicelayer;

/**
 * This is a wrapper exception of all errors in service layer.
 *
 * @author 7oamsz8
 */
public class ServiceLayerException extends RuntimeException {

    // === constructors ===
    ServiceLayerException(String message) {
        super(message);
    }

}
