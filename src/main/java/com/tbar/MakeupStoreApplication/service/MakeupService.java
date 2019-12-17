package com.tbar.MakeupStoreApplication.service;

import com.tbar.MakeupStoreApplication.service.consumer.model.Item;
import com.tbar.MakeupStoreApplication.utility.exceptions.serviceLayer.ServiceLayerException;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;

public interface MakeupService {

    /**
     * This method calls MakeupAPI consumer and return body of it's response.
     * If parameters argument is {@code null} or empty then API response body will return all makeup items.
     * Designed to work best when {@code Map} argument is populated from Controller's {@code @RequestParam Map} annotation.
     *
     * @author 7omasz8
     * @param parameters {@code Map} of parameters to build MakeupAPI request URI.
     * @return not empty list of makeup items.
     * @throws ServiceLayerException when APIConsumer gives error or body of the request is {@code null}.
     */
    List<Item> getProducts(@Nullable Map<String, String> parameters) throws ServiceLayerException;

    /**
     * This method calls MakeupAPI consumer and return body of it's response.
     *
     * @param id unique id number of the product to find.
     * @return makeup item.
     * @throws ServiceLayerException when APIConsumer gives error or body of the request is {@code null}.
     */
    Item getProduct(@NonNull Long id) throws ServiceLayerException;

}
