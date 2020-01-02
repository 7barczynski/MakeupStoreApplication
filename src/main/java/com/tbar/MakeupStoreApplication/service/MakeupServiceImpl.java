package com.tbar.MakeupStoreApplication.service;

import com.tbar.MakeupStoreApplication.service.consumer.MultiAPIConsumer;
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
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
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

    // === constants ===
    private final URI MULTI_BASE_URI;
    private final URI SOLO_BASE_URI;
    private final String SOLO_URI_SUFFIX;
    private final Set<String> VALID_PARAMETERS;

    // === fields ===
    private final MultiAPIConsumer multiAPIConsumer;
    private final SoloAPIConsumer soloAPIConsumer;

    // === constructors ===
    @Autowired
    public MakeupServiceImpl(AppProperties appProperties, MultiAPIConsumer multiAPIConsumer, SoloAPIConsumer soloAPIConsumer) {
        this.MULTI_BASE_URI = URI.create(appProperties.getMakeupApiMultiBaseUri());
        this.SOLO_BASE_URI = URI.create(appProperties.getMakeupApiSoloBaseUri());
        this.SOLO_URI_SUFFIX = appProperties.getMakeupApiSoloUriSuffix();
        this.VALID_PARAMETERS = new HashSet<>(Set.of(appProperties.getMakeupApiValidParameters()));
        this.multiAPIConsumer = multiAPIConsumer;
        this.soloAPIConsumer = soloAPIConsumer;
    }

    // === public methods ===
    @Override
    public List<Item> getProducts(@Nullable Map<String, String> parameters) throws ServiceLayerException {
        // get response and handle consumer layer exception
        URI requestUri = buildUri(parameters);
        ResponseEntity<List<Item>> response;
        try {
            response = multiAPIConsumer.requestData(requestUri);
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
