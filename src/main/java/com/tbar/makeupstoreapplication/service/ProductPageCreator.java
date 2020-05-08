package com.tbar.makeupstoreapplication.service;

import com.tbar.makeupstoreapplication.model.Product;
import com.tbar.makeupstoreapplication.utility.AppProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ProductPageCreator {

    private final int sizeOfProductListOnPage;
    private int currentPage;
    private int indexOfFirstProductOnPage;
    private int sizeOfAllProducts;
    private List<Product> currentPageProducts;

    @Autowired
    public ProductPageCreator(AppProperties appProperties) {
        this.sizeOfProductListOnPage = appProperties.getSizeOfProductListOnPage();
    }

    public Page<Product> createProductPage(@NonNull List<Product> allRequestedProducts, int page) {
        setupFields(allRequestedProducts.size(), page);
        buildProductsForCurrentPage(allRequestedProducts);
        return new PageImpl<>(currentPageProducts,
                PageRequest.of(currentPage, sizeOfProductListOnPage), sizeOfAllProducts);
    }

    private void setupFields(int sizeOfAllProducts, int page) {
        currentPage = page > 1 ? page - 1 : 0;
        indexOfFirstProductOnPage = currentPage * sizeOfProductListOnPage;
        this.sizeOfAllProducts = sizeOfAllProducts;
    }

    private void buildProductsForCurrentPage(List<Product> allProducts) {
        if (isThereAnyProductToGetOnPage()) {
            currentPageProducts = getSubListOfAllProducts(allProducts);
        } else {
            currentPageProducts = Collections.emptyList();
        }
    }

    private boolean isThereAnyProductToGetOnPage() {
        return sizeOfAllProducts > indexOfFirstProductOnPage;
    }

    private List<Product> getSubListOfAllProducts(List<Product> allProducts) {
        int toIndex = Math.min(indexOfFirstProductOnPage + sizeOfProductListOnPage, sizeOfAllProducts);
        return allProducts.subList(indexOfFirstProductOnPage, toIndex);
    }
}
