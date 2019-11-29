package com.tbar.MakeupStoreApplication.service.consumer;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;

import java.net.URI;

/**
 * Functions to request data with from an external API
 * and create response as a {@code ResponseEntity} with body of generic type.
 * Generic type should match parsed type of JSON data of the response.
 *
 * @author 7omasz8
 * @param <T> the body type
 * @see #requestData(URI)
 */
public interface APIConsumer<T> {

    /**
     * Send request and get response from an external API as the ResponseEntity.
     *
     * @param uri URI of the request to the API. Can't be null.
     * @return response from API as an entity
     */
    ResponseEntity<T> requestData(@NonNull URI uri);

}
