
package com.tbar.makeupstoreapplication.service;

import com.tbar.makeupstoreapplication.model.Product;
import com.tbar.makeupstoreapplication.utility.AppProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductPageCreatorTest {

    private ProductPageCreator productPageBuilder;
    private AppProperties appProperties = new AppProperties();
    private int sizeOfProductListOnPage = 12;
    private int sizeOfProductListOnPageDividedByTwo = sizeOfProductListOnPage / 2;
    private List<Product> regularSliceContent = new ArrayList<>(
            Collections.nCopies(sizeOfProductListOnPage, new Product()));
    private List<Product> exampleList;
    private int examplePage;
    private Page<Product> expectedPage;

    @BeforeEach
    void setUp() {
        appProperties.setSizeOfProductListOnPage(sizeOfProductListOnPage);
        productPageBuilder = new ProductPageCreator(appProperties);
        exampleList = new ArrayList<>();
        examplePage = 1;
    }

    @Test
    void given_listAndPage_when_createProductPage_return_pageImpl() {
        exampleList.addAll(Collections.nCopies(sizeOfProductListOnPage * 10, new Product()));
        examplePage = 3;
        expectedPage = new PageImpl<>(regularSliceContent,
                PageRequest.of(examplePage - 1, sizeOfProductListOnPage), exampleList.size());

        Page<Product> actualPage = productPageBuilder.createProductPage(exampleList, examplePage);
        assertEquals(expectedPage, actualPage);
    }

    @Test
    void given_largeListAndLowPage_when_createProductPage_return_pageImplWithCorrectSizeOfContent() {
        exampleList.addAll(Collections.nCopies(sizeOfProductListOnPage * 10, new Product()));
        examplePage = 2;

        Page<Product> actualPage = productPageBuilder.createProductPage(exampleList, examplePage);
        assertEquals(sizeOfProductListOnPage, actualPage.getNumberOfElements());
    }

    @Test
    void given_smallListAndFirstPage_when_createProductPage_return_pageImplWithCorrectSizeOfContent() {
        exampleList.addAll(Collections.nCopies(sizeOfProductListOnPageDividedByTwo, new Product()));

        Page<Product> actualPage = productPageBuilder.createProductPage(exampleList, examplePage);
        assertEquals(sizeOfProductListOnPageDividedByTwo, actualPage.getNumberOfElements());
    }

    @Test
    void given_emptyListAsArgument_when_createProductPage_return_pageImplWithEmptyList() {
        expectedPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(examplePage - 1, sizeOfProductListOnPage), 0);

        Page<Product> actualPage = productPageBuilder.createProductPage(Collections.emptyList(), examplePage);
        assertEquals(expectedPage, actualPage);
    }

    @Test
    void given_tooHighPage_when_createProductPage_return_pageImplWithEmptyList() {
        examplePage = 99999;
        exampleList.addAll(Collections.nCopies(sizeOfProductListOnPage, new Product()));
        expectedPage = new PageImpl<>(Collections.emptyList(),
                PageRequest.of(examplePage - 1, sizeOfProductListOnPage), exampleList.size());

        Page<Product> actualPage = productPageBuilder.createProductPage(exampleList, examplePage);
        assertEquals(expectedPage, actualPage);
    }

    @Test
    void given_pageLessThanOne_when_createProductPage_return_pageImplWithFirstPage() {
        examplePage = -50;
        expectedPage = new PageImpl<>(exampleList, PageRequest.of(0, sizeOfProductListOnPage), exampleList.size());

        Page<Product> actualPage = productPageBuilder.createProductPage(exampleList, examplePage);
        assertEquals(expectedPage.getNumber(), actualPage.getNumber());
    }

    @Test
    void given_page_when_createProductPage_return_pageImplWithPageMinusOne() {
        examplePage = 5;
        exampleList.addAll(Collections.nCopies(sizeOfProductListOnPage * 10, new Product()));

        Page<Product> actualPage = productPageBuilder.createProductPage(exampleList, examplePage);
        assertEquals(examplePage - 1, actualPage.getNumber());
    }
}