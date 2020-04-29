package com.tbar.makeupstoreapplication.service;

import com.tbar.makeupstoreapplication.dao.MakeupAPIConsumer;
import com.tbar.makeupstoreapplication.model.Product;
import com.tbar.makeupstoreapplication.utility.AppProperties;
import com.tbar.makeupstoreapplication.utility.exceptions.ProductsNotFoundException;
import com.tbar.makeupstoreapplication.utility.exceptions.SingleProductNotFoundException;
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
    private final int sizeOfProductListOnPage;

    @Autowired
    public MakeupServiceImpl(MakeupAPIConsumer makeupApiConsumer, PaginationNumbersBuilder paginationNumbersBuilder,
                             AppProperties appProperties) {
        this.makeupApiConsumer = makeupApiConsumer;
        this.paginationNumbersBuilder = paginationNumbersBuilder;
        this.makeupApiUriForCollection = URI.create(appProperties.getMakeupApiUriForCollection());
        this.makeupApiUriForSingleObject = URI.create(appProperties.getMakeupApiUriForSingleObject());
        this.makeupApiSingleObjectiUriSuffix = appProperties.getMakeupApiSingleObjectUriSuffix();
        this.makeupApiValidParameters = new HashSet<>(Set.of(appProperties.getMakeupApiValidParameters()));
        this.sizeOfProductListOnPage = appProperties.getSizeOfProductListOnPage();
    }

    @Override
    public Page<Product> getPaginatedProducts(@Nullable Map<String, String> requestParameters, int page)
            throws ProductsNotFoundException {
        // pages in PageImpl are 0 based
        int currentPage = page - 1;
        int firstProductOnPageIndex = currentPage * sizeOfProductListOnPage;
        List<Product> allProducts = getProductCollection(requestParameters);
        List<Product> currentPageProducts = getProductsForCurrentPage(allProducts, firstProductOnPageIndex);
        log.debug("getPaginatedProducts method. currentPage = {}, currentPageProducts = {}", currentPage, currentPageProducts);
        return new PageImpl<>(currentPageProducts, PageRequest.of(currentPage, sizeOfProductListOnPage), allProducts.size());
    }

    @Override
    public List<Product> getProductCollection(@Nullable Map<String, String> requestParameters)
            throws ProductsNotFoundException {
        URI requestUri = buildUri(requestParameters);
        List<Product> response = makeRequestToApiForCollection(requestUri);
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

    private List<Product> makeRequestToApiForCollection(URI requestUri) {
            return makeupApiConsumer.requestCollection();
    }

    private void isResponseListEmpty(List<Product> responseList) throws ProductsNotFoundException {
        if (responseList == null || responseList.isEmpty()) {
            throw new ProductsNotFoundException();
        }
    }

    private List<Product> getProductsForCurrentPage(List<Product> allProducts, int firstProductOnPageIndex) {
        if (isThereAnyProductToGetOnPage(allProducts.size(), firstProductOnPageIndex)) {
            return getSubListOfProductsAll(allProducts, firstProductOnPageIndex);
        }
        return Collections.emptyList();
    }

    private boolean isThereAnyProductToGetOnPage(int ProductsListSize, int firstProductOnPageIndex) {
        return ProductsListSize > firstProductOnPageIndex;
    }

    private List<Product> getSubListOfProductsAll(List<Product> allProducts, int firstProductOnPageIndex) {
        int toIndex = Math.min(firstProductOnPageIndex + sizeOfProductListOnPage, allProducts.size());
        return allProducts.subList(firstProductOnPageIndex, toIndex);
    }

    @Override
    public Product getProduct(@NonNull Long id) throws SingleProductNotFoundException {
        URI requestUri = buildUri(id);
        Product response = makeRequestToApiForSingleObject(requestUri);
        isResponseProductNull(response);
        log.debug("getProduct method. URI = {}, response body = {}", requestUri, response);
        return response;
    }

    private URI buildUri(@NonNull Long id) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(makeupApiUriForSingleObject);
        uriBuilder.path(id + makeupApiSingleObjectiUriSuffix);
        log.debug("Entered buildUri method with id = {}; Build URI = {}", id, uriBuilder.build());
        return uriBuilder.build().toUri();
    }

    private Product makeRequestToApiForSingleObject(URI requestUri) {
            return null;
    }

    private void isResponseProductNull(Product responseProduct) throws SingleProductNotFoundException {
        if (responseProduct == null) {
            throw new SingleProductNotFoundException();
        }
    }

    @Override
    public List<Integer> getPaginationNumbers(Page<Product> currentPage) {
        List<Integer> paginationNumbers = paginationNumbersBuilder.build(currentPage);
        log.debug("getPaginationNumbers method. paginationNumbers = {}", paginationNumbers);
        return paginationNumbers;
    }
}