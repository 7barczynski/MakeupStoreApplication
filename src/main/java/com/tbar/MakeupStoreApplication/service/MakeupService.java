package com.tbar.MakeupStoreApplication.service;

import com.tbar.MakeupStoreApplication.service.consumer.model.Item;
import com.tbar.MakeupStoreApplication.utility.exceptions.APIConnectionException;
import com.tbar.MakeupStoreApplication.utility.exceptions.ProductNotFoundException;

import java.util.List;
import java.util.Map;

/**
 * This service connects to API consumer and gets response bodies.
 *
 * @author 7omasz8
 */
public interface MakeupService {

    /**
     * This method calls for MakeupAPI consumer and return body of it's response.
     * MakeupAPI proper responses contains only {@code HttpStatus.OK} so this method
     * checks if any other status code is encountered and then throws {@code APIConnectionException}.<br>
     * Designed to work best when {@code Map} argument is populated from Controller's {@code @RequestParam Map} annotation.
     *
     * @param parameters {@code Map} of parameters to build MakeupAPI request
     * @author 7omasz8
     * @return not empty list of makeup items.
     * @throws ProductNotFoundException when body of the response taken from MakeupAPI consumer is empty.
     * @throws APIConnectionException when http status code of the response taken from MakeupAPI consumer is other than 200 (OK).
     */
    List<Item> getProducts(Map<String, String> parameters) throws ProductNotFoundException, APIConnectionException;

}
