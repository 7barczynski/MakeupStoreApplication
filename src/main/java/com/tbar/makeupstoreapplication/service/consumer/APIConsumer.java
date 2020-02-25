package com.tbar.makeupstoreapplication.service.consumer;

import com.tbar.makeupstoreapplication.utility.exceptions.consumerlayer.APICallException;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;

import java.net.URI;

/**
 * Functions to request data from an external API
 * and create response as a {@code ResponseEntity}.
 *
 * @author 7omasz8
 * @see #requestData(URI)
 */
public interface APIConsumer {

    /**
     * Send request and return response from an external API as the ResponseEntity.
     *
     * @param uri URI of the request to the API.
     * @throws APICallException when nothing has been found or there is client or server error.
     * @return response from API as an entity.
     */
    ResponseEntity requestData(@NonNull URI uri) throws APICallException;

}
