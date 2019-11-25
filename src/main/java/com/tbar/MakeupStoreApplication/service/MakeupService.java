package com.tbar.MakeupStoreApplication.service;

import com.tbar.MakeupStoreApplication.service.consumer.model.Item;
import com.tbar.MakeupStoreApplication.utility.exceptions.ExternalAPIException;
import com.tbar.MakeupStoreApplication.utility.exceptions.ProductNotFoundException;

import java.util.List;
import java.util.Map;

/**
 * This service connects to API consumer and send
 * response bodies onward.
 *
 * @author 7omasz8
 */
public interface MakeupService {

    /**
     * This method calls for API consumer of makeup products.
     * It uses {@code URIBuilder} to create a valid {@code URI} for the request to API.
     * If the body of the response is empty or {@code HttpStatus} code is other than 200 (OK)
     * it throws appropriate exception.
     *
     * @param parameters map of all parameters of request URL
     * @author 7omasz8
     * @return List of makeup items
     * @throws ProductNotFoundException when body of the response taken from consumer is empty
     * @throws ExternalAPIException when http status code of the response taken from consumer is other than 200 (OK)
     */
    List<Item> getProducts(Map<String, String> parameters) throws ProductNotFoundException, ExternalAPIException;

}
