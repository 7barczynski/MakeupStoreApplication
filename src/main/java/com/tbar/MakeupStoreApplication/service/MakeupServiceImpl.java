package com.tbar.MakeupStoreApplication.service;

import com.tbar.MakeupStoreApplication.service.consumer.ProductProductConsumer;
import com.tbar.MakeupStoreApplication.service.consumer.SoloAPIConsumer;
import com.tbar.MakeupStoreApplication.service.consumer.model.Item;
import com.tbar.MakeupStoreApplication.utility.AppProperties;
import com.tbar.MakeupStoreApplication.utility.exceptions.consumerLayer.APICallClientSideException;
import com.tbar.MakeupStoreApplication.utility.exceptions.consumerLayer.APICallNotFoundException;
import com.tbar.MakeupStoreApplication.utility.exceptions.consumerLayer.APICallServerSideException;
import com.tbar.MakeupStoreApplication.utility.exceptions.serviceLayer.APIConnectionException;
import com.tbar.MakeupStoreApplication.utility.exceptions.serviceLayer.ProductNotFoundException;
import com.tbar.MakeupStoreApplication.utility.exceptions.serviceLayer.ServiceLayerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
public class MakeupServiceImpl implements MakeupService {

  // === constants ===
  // definicja stałej to "public static final"
  // tutaj masz pola klasy, które są tylko i wyłacznie finalne, ale każda instancja tej klasy miałaby
  // to pole osobno zainicjalizowane

  // więc albo dodajemy statuc, albo zmieniamy nazwy na camelCase, np. multiBaseURI, validParameters itd
  private final URI MULTI_BASE_URI;
  private final URI SOLO_BASE_URI;
  private final String SOLO_URI_SUFFIX;
  private final Set<String> VALID_PARAMETERS;
  private final int PAGINATION_NUMBERS_SIZE;
  private final int PAGINATION_LEFT_OFFSET;

  // === fields ===
  // nie potrzebujesz tych dwóch konsumentów, wystarczy jeden.
  private final ProductProductConsumer productAPIConsumer;

  // === constructors ===
  @Autowired
  public MakeupServiceImpl(AppProperties appProperties, ProductProductConsumer productAPIConsumer) {
    this.VALID_PARAMETERS = new HashSet<>(Set.of(appProperties.getMakeupApiValidParameters()));
    this.PAGINATION_NUMBERS_SIZE = appProperties.getPaginationNumbersSize();
    this.PAGINATION_LEFT_OFFSET = appProperties.getPaginationLeftOffset();
    this.productAPIConsumer = productAPIConsumer;
    this.MULTI_BASE_URI = URI.create(appProperties.getMakeupApiMultiBaseUri());
    this.SOLO_BASE_URI = URI.create(appProperties.getMakeupApiSoloBaseUri());
    this.SOLO_URI_SUFFIX = appProperties.getMakeupApiSoloUriSuffix();
  }

  // === public methods ===
  @Override
  public Page<Item> getPaginatedProducts(@Nullable Map<String, String> parameters, int page,
      int size) throws ServiceLayerException {
    List<Item> items = getProducts(parameters);
    int currentPage = page - 1;
    int startItem = currentPage * size;
    List<Item> itemsListOnPage;

    // if there is no item to show send foward an empty list
    if (items.size() < startItem) {
      itemsListOnPage = Collections.emptyList();
    } else {
      int toIndex = Math.min(startItem + size, items.size());
      itemsListOnPage = items.subList(startItem, toIndex);
    }
    log.debug("getPaginatedProducts method. currentPage = {}, startItem = {}, itemsListOnPage = {}",
        currentPage, startItem, itemsListOnPage);

    return new PageImpl<>(itemsListOnPage, PageRequest.of(currentPage, size), items.size());
  }

  @Override
  public List<Item> getProducts(@Nullable Map<String, String> parameters)
      throws ServiceLayerException {
    // get response and handle consumer layer exception
    URI requestUri = buildUri(parameters);

    return productAPIConsumer.requestCollection(requestUri);

    ResponseEntity<List<Item>> response;
    try {
      response = productAPIConsumer.requestData(requestUri);
    } catch (APICallNotFoundException e) {
      throw new ProductNotFoundException(requestUri.toString(), parameters);
    } catch (APICallClientSideException | APICallServerSideException e) {
      throw new APIConnectionException(requestUri.toString(), parameters);
    }
    log.debug("getProducts method. URI = {}, ResponseEntity = {}", requestUri, response);

    // check if response body is not null and empty then return body
    if (response.getBody() != null && !response.getBody().isEmpty()) {
      return response.getBody();
    } else {
      throw new ProductNotFoundException(requestUri.toString(), parameters);
    }
  }

  @Override
  public Item getProduct(@NonNull Long id) throws ServiceLayerException {
    // get response and handle consumer layer exception
    URI requestUri = buildUri(id);
    ResponseEntity<Item> response;
    try {
      response = soloAPIConsumer.requestData(requestUri);
    } catch (APICallNotFoundException e) {
      throw new ProductNotFoundException(requestUri.toString(), id);
    } catch (APICallClientSideException | APICallServerSideException e) {
      throw new APIConnectionException(requestUri.toString(), id);
    }
    log.debug("getProduct method. URI = {}, ResponseEntity = {}", requestUri, response);

    // check if response body is not null then return body
    if (response.getBody() != null) {
      return response.getBody();
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
      int currentPageMinusOffset = itemsPage.getNumber() - PAGINATION_LEFT_OFFSET + 1;
      int totalPagesMinusSize = totalPages - PAGINATION_NUMBERS_SIZE + 1;
      int offsetNumberPlusSize = currentPageMinusOffset + PAGINATION_NUMBERS_SIZE - 1;

      // if current page number is lower than offset or there are less pages than max pagination size
      if (currentPageMinusOffset <= 1 || totalPagesMinusSize <= 1) {
        firstNumber = 1;
        lastNumber = Math.min(PAGINATION_NUMBERS_SIZE, totalPages);
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
    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(MULTI_BASE_URI);
    if (parameters != null) {
      // loop through arguments parameters to find and add valid ones
      for (Map.Entry<String, String> entry : parameters.entrySet()) {
        if (VALID_PARAMETERS.contains(entry.getKey())) {
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
    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(SOLO_BASE_URI);

    uriBuilder.path(id + SOLO_URI_SUFFIX);

    log.debug("Entered buildUri method with id = {}; Build URI = {}", id, uriBuilder.build());
    return uriBuilder.build().toUri();
  }

}
