package com.tbar.MakeupStoreApplication.utility.exceptions.serviceLayer;

public class ProductNotFoundException extends ServiceLayerException {

  // === constants ===
  private static final String MESSAGE = "Product cannot be found.";

  // === constructors ===
  public ProductNotFoundException(String requestUri) {
    super(MESSAGE + " Request URI = " + requestUri);
  }

  public ProductNotFoundException(String requestUri, Long id) {
    super(MESSAGE + " Request URI = " + requestUri + ", ID = " + id);
  }
}
