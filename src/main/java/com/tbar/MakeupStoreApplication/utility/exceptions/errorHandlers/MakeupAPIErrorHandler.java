package com.tbar.MakeupStoreApplication.utility.exceptions.errorHandlers;

import com.tbar.MakeupStoreApplication.utility.exceptions.APICallClientSideException;
import com.tbar.MakeupStoreApplication.utility.exceptions.APICallServerSideException;
import com.tbar.MakeupStoreApplication.utility.exceptions.ProductNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;

/**
 * This error handler is designed to check and throw appropriate exception on:
 * <ul>
 *     <li>server side errors</li>
 *     <li>client side errors (except 404 error)</li>
 *     <li>404 (NOT_FOUND) error</li>
 * </ul>
 *
 * @author 7omasz8
 */
@Slf4j
@Component
public class MakeupAPIErrorHandler implements ResponseErrorHandler {
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return (response.getStatusCode().series() == CLIENT_ERROR
                || response.getStatusCode().series() == SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        if (response.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR) {
            throw new APICallServerSideException(response.getStatusCode().value());
        } else if (response.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR) {
            if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ProductNotFoundException();
            } else {
                throw new APICallClientSideException(response.getStatusCode().value());
            }
        }
    }
}
