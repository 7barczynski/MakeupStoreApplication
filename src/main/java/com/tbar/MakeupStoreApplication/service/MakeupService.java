package com.tbar.MakeupStoreApplication.service;

import com.tbar.MakeupStoreApplication.service.consumer.model.Item;
import com.tbar.MakeupStoreApplication.utility.exceptions.serviceLayer.ServiceLayerException;
import org.springframework.data.domain.Page;
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

    /**
     * This method calls MakeupAPI consumer through {@link MakeupService#getProducts(Map) getProducts()} method
     * and create from the gained list and request's pagination informations a {@code Page}.
     * If parameters argument is {@code null} or empty then API response body will return all makeup items.
     * Designed to work best when {@code Map} argument is populated from Controller's {@code @RequestParam Map} annotation.
     *
     * @param parameters {@code Map} of parameters to call {@link MakeupService#getProducts(Map) getProducts()} method.
     * @param page requested page number.
     * @param size number of items on a page.
     * @return one page of items.
     * @throws ServiceLayerException when APIConsumer gives error or body of the request is {@code null}.
     */
    Page<Item> getPaginatedProducts(@Nullable Map<String, String> parameters, int page, int size) throws ServiceLayerException;

    /**
     * This method create a list of pagination numbers which can be link directly to the application view.
     * Some values needed to create that list are changeable through properties file.
     *
     * @param itemsPage where pagination information comes from.
     * @return list of page numbers or null if there are no items
     */
    List<Integer> getPaginationNumbers(@NonNull Page<Item> itemsPage);

}
