package com.tbar.makeupstoreapplication.utility;

import com.tbar.makeupstoreapplication.utility.exceptions.APIConnectionException;
import com.tbar.makeupstoreapplication.utility.exceptions.ProductsNotFoundException;
import com.tbar.makeupstoreapplication.utility.exceptions.SingleProductNotFoundException;

public class ExceptionHandlerUtilities {

    public enum ExceptionCase {
        PRODUCTS_NOT_FOUND_EXCEPTION,
        SINGLE_PRODUCT_NOT_FOUND_EXCEPTION,
        API_CONNECTION_EXCEPTION,
        OTHER_EXCEPTION
    }

    public static ExceptionCase chooseSpecificException(Exception exception) {
        if (exception instanceof ProductsNotFoundException) {
            return ExceptionCase.PRODUCTS_NOT_FOUND_EXCEPTION;
        } else if (exception instanceof SingleProductNotFoundException) {
            return ExceptionCase.SINGLE_PRODUCT_NOT_FOUND_EXCEPTION;
        } else if (exception instanceof APIConnectionException) {
            return ExceptionCase.API_CONNECTION_EXCEPTION;
        }
        return ExceptionCase.OTHER_EXCEPTION;
    }
}
