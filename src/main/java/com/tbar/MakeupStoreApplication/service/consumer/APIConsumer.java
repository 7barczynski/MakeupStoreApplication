package com.tbar.MakeupStoreApplication.service.consumer;

import org.springframework.http.ResponseEntity;

import javax.validation.constraints.NotNull;
import java.net.URI;

/**
 * Functions to request data from an external API and create response as a
 * {@code ResponseEntity} with body of generic type. Generic type should match
 * parsed type of JSON data of the response.
 *
 * @author 7omasz8
 * @param <T> the body type
 * @see #requestData(URI)
 */
public interface APIConsumer<T> {

    /**
     * Get response from an external API as the ResponseEntity.
     * When an {@code URI} is null or {@code RestTemplate} encounters
     * errors on the API side it returns {@code ResponseEntity} with empty
     * body and proper {@code HttpStatus} code.
     *
     * @param uri URI of the request to the API. Can't be null.
     * @return response from API as an entity
     */
    ResponseEntity<T> requestData(@NotNull URI uri);

}
