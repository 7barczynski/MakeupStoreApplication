package com.tbar.MakeupStoreApplication.service;

import com.tbar.MakeupStoreApplication.service.consumer.model.Item;
import com.tbar.MakeupStoreApplication.utility.exceptions.APIConnectionException;
import com.tbar.MakeupStoreApplication.utility.exceptions.ProductNotFoundException;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;

public interface MakeupService {

    /**
     * This method calls MakeupAPI consumer and return body of it's response.
     * MakeupAPI proper responses contains only {@code HttpStatus.OK} so this method
     * checks if any other status code is encountered and then throws {@code APIConnectionException}.<br>
     * If parameters argument is {@code null} or empty then API response body will return all makeup items.
     * Designed to work best when {@code Map} argument is populated from Controller's {@code @RequestParam Map} annotation.
     *
     * @param parameters {@code Map} of parameters to build MakeupAPI request
     * @author 7omasz8
     * @return not empty list of makeup items.
     * @throws ProductNotFoundException when body of the response taken from MakeupAPI consumer is empty.
     * @throws APIConnectionException when http status code of the response taken from MakeupAPI consumer is other than 200 (OK).
     */
    List<Item> getProducts(@Nullable Map<String, String> parameters) throws ProductNotFoundException, APIConnectionException;

    /**
     * This method calls MakeupAPI consumer and return body of it's response.
     * MakeupAPI proper responses contains only {@code HttpStatus.OK} and {@code HttpStatus.NOT_FOUND} so this method
     * checks if any other status code is encountered and then throws {@code APIConnectionException}.<br>
     *
     * @param id unique id number of the product to find
     * @return makeup item
     * @throws ProductNotFoundException when http status code of the response is 404 or the response body is {@code null}
     * @throws APIConnectionException when http status code of the response is other than 200 or 404
     */
    Item getProduct(@NonNull Long id) throws ProductNotFoundException, APIConnectionException;

}
