package com.tbar.MakeupStoreApplication.service;

import com.tbar.MakeupStoreApplication.service.consumer.APIConsumerImpl;
import com.tbar.MakeupStoreApplication.service.consumer.model.Item;
import com.tbar.MakeupStoreApplication.utility.exceptions.APIConnectionException;
import com.tbar.MakeupStoreApplication.utility.exceptions.ProductNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class MakeupServiceImpl implements MakeupService {

    // === fields ===
    private URI baseUri;
    private Set<String> validParameters;
    private APIConsumerImpl<List<Item>> makeupAPIConsumer;

    // === constructors ===
    @Autowired
    public MakeupServiceImpl(APIConsumerImpl<List<Item>> makeupAPIConsumer) {
        this.makeupAPIConsumer = makeupAPIConsumer;
    }

    // === setters ===
    @Value("${application.makeup.api.base.uri}")
    private void setBaseUri(String baseUri) {
        this.baseUri = URI.create(baseUri);
    }

    @Value("${application.makeup.api.valid.parameters}")
    private void setValidParameters(String[] validParameters) {
        this.validParameters = new HashSet<>(Set.of(validParameters));
    }

    // === public methods ===
    @Override
    public List<Item> getProducts(@Nullable Map<String, String> parameters) throws ProductNotFoundException, APIConnectionException {
        // get response
        URI requestUri = buildUri(parameters);
        ResponseEntity<List<Item>> response = makeupAPIConsumer.requestData(requestUri);
        // check if response is ok and not null then return body
        if (response.getStatusCode() == HttpStatus.OK) {
            if (!response.getBody().isEmpty()) {
                return response.getBody();
            } else {
                throw new ProductNotFoundException(requestUri.toString());
            }
        } else {
            throw new APIConnectionException(response.getStatusCodeValue(), requestUri.toString());
        }
    }

    // === private methods ===
    /**
     * It extends <i>baseUri</i> field with query parameters from {@code Map} argument.<br>
     * Method checks if parameters provided in argument {@code Map} match with
     * the valid ones from <i>validParameters</i> field.<br>
     * If parameters {@code Map} argument is {@code null} then method returns <i>baseUri</i>.<br>
     *
     * @param parameters {@code Map} of query parameters to be added. Can be {@code null}.
     * @return {@code URI} build with valid (or all) query parameters.
     */
    private URI buildUri(@Nullable Map<String, String> parameters) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(baseUri);
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
}
