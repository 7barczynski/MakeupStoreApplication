package com.tbar.makeupstoreapplication.service;

import com.tbar.makeupstoreapplication.service.consumer.MakeupAPIConsumer;
import com.tbar.makeupstoreapplication.service.consumer.model.Item;
import com.tbar.makeupstoreapplication.utility.AppProperties;
import com.tbar.makeupstoreapplication.utility.exceptions.APICallException;
import com.tbar.makeupstoreapplication.utility.exceptions.servicelayer.APIConnectionException;
import com.tbar.makeupstoreapplication.utility.exceptions.servicelayer.ProductNotFoundException;
import com.tbar.makeupstoreapplication.utility.exceptions.servicelayer.ServiceLayerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
public class MakeupServiceImpl implements MakeupService {

    // === constants ===
    private final URI baseUriForCollection;
    private final URI baseUriForSingleObject;
    private final String singleObjectiUriSuffix;
    private final Set<String> validParameters;
    private final int paginationNumbersSize;
    private final int paginationLeftOffset;

    // === fields ===
    private final MakeupAPIConsumer makeupAPIConsumer;

    // === constructors ===
    @Autowired
    public MakeupServiceImpl(AppProperties appProperties, MakeupAPIConsumer makeupAPIConsumer) {
        this.baseUriForCollection = URI.create(appProperties.getMakeupApiBaseUriForCollection());
        this.baseUriForSingleObject = URI.create(appProperties.getMakeupApiBaseUriForSingleObject());
        this.singleObjectiUriSuffix = appProperties.getMakeupApiSingleObjectUriSuffix();
        this.validParameters = new HashSet<>(Set.of(appProperties.getMakeupApiValidParameters()));
        this.paginationNumbersSize = appProperties.getPaginationNumbersSize();
        this.paginationLeftOffset = appProperties.getPaginationLeftOffset();
        this.makeupAPIConsumer = makeupAPIConsumer;
    }

    // === public methods ===
    @Override
    public Page<Item> getPaginatedProducts(@Nullable Map<String, String> parameters, int page, int size) throws ServiceLayerException {
        List<Item> items = getProducts(parameters);
        int currentPage = page-1;
        int startItem = currentPage * size;
        List<Item> itemsListOnPage;

        // if there is no item to show send foward an empty list
        if (items.size() < startItem) {
            itemsListOnPage = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + size, items.size());
            itemsListOnPage = items.subList(startItem, toIndex);
        }
        log.debug("getPaginatedProducts method. currentPage = {}, startItem = {}, itemsListOnPage = {}", currentPage, startItem, itemsListOnPage);

        return new PageImpl<>(itemsListOnPage, PageRequest.of(currentPage, size), items.size());
    }

    @Override
    public List<Item> getProducts(@Nullable Map<String, String> parameters) throws ServiceLayerException {
        // get response and handle consumer layer exception
        URI requestUri = buildUri(parameters);
        List<Item> response;
        try {
            response = makeupAPIConsumer.requestCollection(requestUri);
        } catch (APICallException e) {
            throw new APIConnectionException(requestUri.toString(), parameters);
        }
        log.debug("getProducts method. URI = {}, ResponseEntity = {}", requestUri, response);

        // check if response body is not null and empty then return body
        if (response != null && !response.isEmpty()) {
            return response;
        } else {
            throw new ProductNotFoundException(requestUri.toString(), parameters);
        }
    }

    @Override
    public Item getProduct(@NonNull Long id) throws ServiceLayerException {
        // get response and handle consumer layer exception
        URI requestUri = buildUri(id);
        Item response;
        try {
            response = makeupAPIConsumer.requestSingleObject(requestUri);
        } catch (APICallException e) {
            throw new APIConnectionException(requestUri.toString(), id);
        }
        log.debug("getProduct method. URI = {}, ResponseEntity = {}", requestUri, response);

        // check if response body is not null then return body
        if (response != null) {
            return response;
        } else {
            throw new ProductNotFoundException(requestUri.toString(), id);
        }
    }

    @Override
    public List<Integer> getPaginationNumbers(@NonNull Page<Item> itemsPage) {
        List<Integer> paginationNumbers = null;
        int totalPages = itemsPage.getTotalPages();

        if (totalPages > 1 && itemsPage.getNumber() < totalPages) {
            int firstNumber;
            int lastNumber;
            int currentPageMinusOffset = itemsPage.getNumber() - paginationLeftOffset + 1;
            int totalPagesMinusSize = totalPages - paginationNumbersSize + 1;
            int offsetNumberPlusSize = currentPageMinusOffset + paginationNumbersSize - 1;

            // if current page number is lower than offset or there are less pages than max pagination size
            if (currentPageMinusOffset <= 1 || totalPagesMinusSize <= 1) {
                firstNumber = 1;
                lastNumber = Math.min(paginationNumbersSize, totalPages);
            // if current page number is reaching end of a numbers list and offset should be disabled
            } else if (offsetNumberPlusSize >= totalPages) {
                firstNumber = totalPagesMinusSize;
                lastNumber = totalPages;
            } else {
                firstNumber = currentPageMinusOffset;
                lastNumber = offsetNumberPlusSize;
            }

            paginationNumbers = IntStream.rangeClosed(firstNumber, lastNumber)
                    .boxed()
                    .collect(Collectors.toList());
        }
        log.debug("getPaginationNumbers method. paginationNumbers = {}", paginationNumbers);

        return paginationNumbers;
    }

    // === private methods ===
    /**
     * It extends <i>multiSearchBaseUri</i> field with query parameters from {@code Map} argument.<br>
     * Method checks if parameters provided in argument {@code Map} match with
     * the valid ones from <i>validParameters</i> field.<br>
     * If parameters {@code Map} argument is {@code null} then method returns <i>multiSearchBaseUri</i>.<br>
     *
     * @param parameters {@code Map} of query parameters to be added. Can be {@code null}.
     * @return {@code URI} build with valid (or all) query parameters.
     */
    private URI buildUri(@Nullable Map<String, String> parameters) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(baseUriForCollection);
        if (parameters != null) {
            // loop through arguments parameters to find and add valid ones
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                if (validParameters.contains(entry.getKey())) {
                    uriBuilder.queryParam(entry.getKey(), entry.getValue());
                }
            }
        }
        log.debug("Entered buildUri method with map of parameters = {}; Build URI = {}", parameters, uriBuilder.build());
        return uriBuilder.build().toUri();
    }

    /**
     * It extends <i>soloSearchBaseUri</i> field with path element containing <i>id</i> argument.<br>
     *
     * @param id unique product id added to {@code URI} path
     * @return {@code URI} build with id in path
     */
    private URI buildUri(@NonNull Long id) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(baseUriForSingleObject);

        uriBuilder.path(id + singleObjectiUriSuffix);

        log.debug("Entered buildUri method with id = {}; Build URI = {}", id, uriBuilder.build());
        return uriBuilder.build().toUri();
    }
}