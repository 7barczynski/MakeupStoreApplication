package com.tbar.makeupstoreapplication.service;

import com.tbar.makeupstoreapplication.service.consumer.MakeupAPIConsumer;
import com.tbar.makeupstoreapplication.service.consumer.model.Item;
import com.tbar.makeupstoreapplication.utility.AppProperties;
import com.tbar.makeupstoreapplication.utility.exceptions.APIConnectionException;
import com.tbar.makeupstoreapplication.utility.exceptions.ProductNotFoundException;
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

@Slf4j
@Service
public class MakeupServiceImpl implements MakeupService {

    private final MakeupAPIConsumer makeupApiConsumer;
    private final PaginationNumbersBuilder paginationNumbersBuilder;
    private final URI makeupApiUriForCollection;
    private final URI makeupApiUriForSingleObject;
    private final String makeupApiSingleObjectiUriSuffix;
    private final Set<String> makeupApiValidParameters;
    private final int pageItemListSize;

    @Autowired
    public MakeupServiceImpl(MakeupAPIConsumer makeupApiConsumer, PaginationNumbersBuilder paginationNumbersBuilder,
                             AppProperties appProperties) {
        this.makeupApiConsumer = makeupApiConsumer;
        this.paginationNumbersBuilder = paginationNumbersBuilder;
        this.makeupApiUriForCollection = URI.create(appProperties.getMakeupApiUriForCollection());
        this.makeupApiUriForSingleObject = URI.create(appProperties.getMakeupApiUriForSingleObject());
        this.makeupApiSingleObjectiUriSuffix = appProperties.getMakeupApiSingleObjectUriSuffix();
        this.makeupApiValidParameters = new HashSet<>(Set.of(appProperties.getMakeupApiValidParameters()));
        this.pageItemListSize = appProperties.getPageItemListSize();
    }

    @Override
    public Page<Item> getPaginatedProducts(@Nullable Map<String, String> requestParameters, int page)
            throws ProductNotFoundException, APIConnectionException {
        // pages in PageImpl are 0 based
        int currentPage = page - 1;
        int firstItemOnPageIndex = currentPage * pageItemListSize;
        List<Item> allItems = getProducts(requestParameters);
        List<Item> currentPageItems = getItemsForCurrentPage(allItems, firstItemOnPageIndex);
        log.debug("getPaginatedProducts method. currentPage = {}, currentPageItems = {}", currentPage, currentPageItems);
        return new PageImpl<>(currentPageItems, PageRequest.of(currentPage, pageItemListSize), allItems.size());
    }

    @Override
    public List<Item> getProducts(@Nullable Map<String, String> requestParameters)
            throws ProductNotFoundException, APIConnectionException {
        URI requestUri = buildUri(requestParameters);
        List<Item> response = makeRequestToApiForCollection(requestUri);
        isResponseListEmpty(response);
        log.debug("getProducts method. URI = {}, response body = {}", requestUri, response);
        return response;
    }

    private URI buildUri(@Nullable Map<String, String> requestParameters) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(makeupApiUriForCollection);
        if (requestParameters != null) {
            addValidParametersToUri(requestParameters, uriBuilder);
        }
        log.debug("Entered buildUri method with map of parameters = {}; Build URI = {}",
                requestParameters, uriBuilder.build());
        return uriBuilder.build().toUri();
    }

    private void addValidParametersToUri(Map<String, String> requestParameters, UriComponentsBuilder uriBuilder) {
        for (Map.Entry<String, String> entry : requestParameters.entrySet()) {
            if (makeupApiValidParameters.contains(entry.getKey())) {
                uriBuilder.queryParam(entry.getKey(), entry.getValue());
            }
        }
    }

    private List<Item> makeRequestToApiForCollection(URI requestUri) throws APIConnectionException {
            return makeupApiConsumer.requestCollection(requestUri);
    }

    private void isResponseListEmpty(List<Item> responseList) throws ProductNotFoundException {
        if (responseList == null || responseList.isEmpty()) {
            throw new ProductNotFoundException();
        }
    }

    private List<Item> getItemsForCurrentPage(List<Item> allItems, int firstItemOnPageIndex) {
        if (isThereAnyItemToGetOnPage(allItems.size(), firstItemOnPageIndex)) {
            return getSubListOfItemsAll(allItems, firstItemOnPageIndex);
        }
        return Collections.emptyList();
    }

    private boolean isThereAnyItemToGetOnPage(int itemsListSize, int firstItemOnPageIndex) {
        return itemsListSize > firstItemOnPageIndex;
    }

    private List<Item> getSubListOfItemsAll(List<Item> allItems, int firstItemOnPageIndex) {
        int toIndex = Math.min(firstItemOnPageIndex + pageItemListSize, allItems.size());
        return allItems.subList(firstItemOnPageIndex, toIndex);
    }

    @Override
    public Item getProduct(@NonNull Long id) throws ProductNotFoundException, APIConnectionException {
        URI requestUri = buildUri(id);
        Item response = makeRequestToApiForSingleObject(requestUri);
        isResponseItemNull(response);
        log.debug("getProduct method. URI = {}, response body = {}", requestUri, response);
        return response;
    }

    private URI buildUri(@NonNull Long id) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(makeupApiUriForSingleObject);
        uriBuilder.path(id + makeupApiSingleObjectiUriSuffix);
        log.debug("Entered buildUri method with id = {}; Build URI = {}", id, uriBuilder.build());
        return uriBuilder.build().toUri();
    }

    private Item makeRequestToApiForSingleObject(URI requestUri) throws APIConnectionException {
            return makeupApiConsumer.requestSingleObject(requestUri);
    }

    private void isResponseItemNull(Item responseItem) throws ProductNotFoundException {
        if (responseItem == null) {
            throw new ProductNotFoundException();
        }
    }

    @Override
    public List<Integer> getPaginationNumbers(Page<Item> currentPage) {
        List<Integer> paginationNumbers = paginationNumbersBuilder.build(currentPage);
        log.debug("getPaginationNumbers method. paginationNumbers = {}", paginationNumbers);
        return paginationNumbers;
    }
}