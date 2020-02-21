package com.tbar.MakeupStoreApplication.service.consumer;

import com.tbar.MakeupStoreApplication.service.consumer.model.Item;
import com.tbar.MakeupStoreApplication.utility.AppProperties;
import com.tbar.MakeupStoreApplication.utility.exceptions.consumerLayer.APICallClientSideException;
import com.tbar.MakeupStoreApplication.utility.exceptions.consumerLayer.APICallException;
import com.tbar.MakeupStoreApplication.utility.exceptions.consumerLayer.APICallNotFoundException;
import com.tbar.MakeupStoreApplication.utility.exceptions.consumerLayer.APICallServerSideException;
import com.tbar.MakeupStoreApplication.utility.exceptions.serviceLayer.ProductNotFoundException;
import java.net.URI;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class ProductProductConsumer implements ProductConsumer<Item> {


  // === fields ===
  private RestTemplate restTemplate;

  // === constructors ===
  @Autowired
  public ProductProductConsumer(AppProperties appProperties, RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  // === public methods ===

  /**
   * Send {@code HttpMethod.GET} request and get response from an external API as the
   * ResponseEntity.
   *
   * @param uri URI of the request to the API. Can't be {@code null}.
   * @return response from API as an entity
   * @throws APICallException when nothing has been found or there is client or server error.
   */
  @Override
  public List<Item> requestCollection(@NonNull URI uri) throws APICallException {
    try {
      ResponseEntity<List<Item>> response = restTemplate
          .exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
          });
      return response.getBody();
//            response = productAPIConsumer.requestData(requestUri);
    } catch (APICallNotFoundException e) {
      // ten wyjątek nie jest rzucany jak ProductNotFound tylko kiedy nie znaleziono URLa (404). Zły wyjątek
      throw new ProductNotFoundException(uri.toString());
    } catch (APICallClientSideException | APICallServerSideException e) {
      // przechwytujesz tutaj specyficzne wyjątki tylko po to, żeby wyrzucić generalny APIConnectionException
      // nie ma to żadnego sensu
      throw new APIConnectionException(uri.toString(), parameters);
    }
    log.debug("getProducts method. URI = {}, ResponseEntity = {}", requestUri, response);

    // check if response body is not null and empty then return body
    if (response.getBody() != null && !response.getBody().isEmpty()) {
      return response.getBody();
    } else {
      throw new ProductNotFoundException(requestUri.toString(), parameters);
    }
    ResponseEntity<List<Item>> response = restTemplate
        .exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
        });
    log.debug("Response taken from external API. Status code = {}; URI = {}; Body = {}",
        response.getStatusCode(), uri, response.getBody());
    return response;
  }

  // === public methods ===

  /**
   * Send {@code HttpMethod.GET} request and get response from an external API as the
   * ResponseEntity.
   *
   * @param uri URI of the request to the API. Can't be {@code null}.
   * @return response from API as an entity.
   * @throws APICallException when nothing has been found or there is client or server error.
   */
  @Override
  // jak dla mnie to parametrem wejściowym nie powinien być URI tylko atrybuty wyszukiwania
  public Item requestSingleData(@NonNull URI uri) throws APICallException {
    ResponseEntity<Item> response = restTemplate.getForEntity(uri, Item.class);
    log.debug("Response taken from external API. Status code = {}; URI = {}; Body = {}",
        response.getStatusCode(), uri, response.getBody());
    return response;
  }

}
