package com.tbar.makeupstoreapplication.service;

import com.tbar.makeupstoreapplication.service.consumer.model.Item;
import com.tbar.makeupstoreapplication.utility.exceptions.APIConnectionException;
import com.tbar.makeupstoreapplication.utility.exceptions.ProductNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;

public interface MakeupService {

    Page<Item> getPaginatedProducts(@Nullable Map<String, String> requestParameters, int page)
            throws ProductNotFoundException, APIConnectionException;

    List<Item> getProducts(@Nullable Map<String, String> requestParameters)
            throws ProductNotFoundException, APIConnectionException;

    Item getProduct(@NonNull Long id) throws ProductNotFoundException, APIConnectionException;

    List<Integer> getPaginationNumbers(Page<Item> currentPage);
}
