package com.tbar.MakeupStoreApplication.utility.exceptions.errorHandlers;

import com.tbar.MakeupStoreApplication.utility.exceptions.consumerLayer.APICallClientSideException;
import com.tbar.MakeupStoreApplication.utility.exceptions.consumerLayer.APICallNotFoundException;
import com.tbar.MakeupStoreApplication.utility.exceptions.consumerLayer.APICallServerSideException;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

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
    return response.getStatusCode().is4xxClientError() || response.getStatusCode()
        .is5xxServerError();
  }

  @Override
  // 1. po to jest metoda hasError, żeby być pewnym, że tutaj będzie błąd na pewno
  // - taki jak zdefiniujesz w metodzie wyżej. nie potrzebujesz innego sprawdzania
  // 2. nie wiem czy wyrzucanie tutaj Runtime
  public void handleError(ClientHttpResponse response) throws IOException, NullPointerException {
    log.debug("Handling response error from MakeupAPI. Response status code = {}",
        response.getStatusCode());
    if (response.getStatusCode().is5xxServerError()) {
      throw new APICallServerSideException(response.getStatusCode().value());
    }
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new APICallNotFoundException();
    }
    throw new APICallClientSideException(response.getStatusCode().value());
  }

}
