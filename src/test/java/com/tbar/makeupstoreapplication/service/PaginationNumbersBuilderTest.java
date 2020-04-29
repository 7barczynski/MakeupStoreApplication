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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PaginationNumbersBuilderTest {

    private final int stubPaginationNumbersSize = 10;
    private final int stubPaginationOffset = 4;
    private final int stubSizeOfProductListOnPage = 12;

    private final List<Product> expectedList = new ArrayList<>(
            Collections.nCopies(stubPaginationNumbersSize * stubSizeOfProductListOnPage * 2, new Product()));
    private final List<Product> productsListOnPage = Collections.nCopies(stubSizeOfProductListOnPage, new Product());
    private final List<Product> listSmallerThanPaginationSize = new ArrayList<>(
            Collections.nCopies(stubPaginationNumbersSize * stubSizeOfProductListOnPage / 2, new Product()));
    private final List<Product> listSmallerThanPageListSize = new ArrayList<>(
            Collections.nCopies(stubSizeOfProductListOnPage / 2, new Product()));

    private final int pageNumberMiddle = stubPaginationOffset * 2;
    private final int pageNumberSmallerThanOffset = stubPaginationOffset / 2;
    private final int pageNumberCloseToMax = expectedList.size() / stubSizeOfProductListOnPage - stubPaginationOffset;

    private final PageRequest pageRequestMiddleNumber = PageRequest.of(pageNumberMiddle - 1, stubSizeOfProductListOnPage);
    private final PageRequest pageRequestLowNumber = PageRequest.of(
            pageNumberSmallerThanOffset - 1, stubSizeOfProductListOnPage);
    private final PageRequest pageRequestHighNumber = PageRequest.of(pageNumberCloseToMax - 1, stubSizeOfProductListOnPage);
    private final PageRequest pageRequestTooHighNumber = PageRequest.of(99999, stubSizeOfProductListOnPage);
    private final PageRequest pageRequestZeroNumber = PageRequest.of(0, stubSizeOfProductListOnPage);

    private final Page<Product> expectedPage = new PageImpl<>(
            productsListOnPage, pageRequestMiddleNumber, expectedList.size());
    private final Page<Product> pageWithLowNumber = new PageImpl<>(
            productsListOnPage, pageRequestLowNumber, expectedList.size());
    private final Page<Product> pageWithHighNumber = new PageImpl<>(
            productsListOnPage, pageRequestHighNumber, expectedList.size());
    private final Page<Product> pageWithTooHighNumber = new PageImpl<>(
            Collections.emptyList(), pageRequestTooHighNumber, expectedList.size());
    private final Page<Product> pageSmaller = new PageImpl<>(
            productsListOnPage, pageRequestLowNumber, listSmallerThanPaginationSize.size());
    private final Page<Product> onePage = new PageImpl<>(
            listSmallerThanPageListSize, pageRequestZeroNumber, listSmallerThanPageListSize.size());

    private final List<Integer> expectedNumbersFromOne = IntStream.rangeClosed(1, stubPaginationNumbersSize)
            .boxed()
            .collect(Collectors.toList());
    private final List<Integer> expectedNumbersFromOffset = IntStream.rangeClosed(
            expectedPage.getNumber()+1- stubPaginationOffset,
            expectedPage.getNumber() - stubPaginationOffset + stubPaginationNumbersSize)
            .boxed()
            .collect(Collectors.toList());
    private final List<Integer> expectedNumbersCloseToEnd = IntStream.rangeClosed(
            pageWithHighNumber.getTotalPages()+1- stubPaginationNumbersSize, pageWithHighNumber.getTotalPages())
            .boxed()
            .collect(Collectors.toList());
    private final List<Integer> expectedNumbersSmallerThanSize = IntStream.rangeClosed(1, pageSmaller.getTotalPages())
            .boxed()
            .collect(Collectors.toList());

    private AppProperties appProperties = new AppProperties();
    private PaginationNumbersBuilder paginationNumbersBuilder;

    PaginationNumbersBuilderTest() {
        appProperties.setPaginationNumbersSize(stubPaginationNumbersSize);
        appProperties.setPaginationLeftOffset(stubPaginationOffset);
        appProperties.setSizeOfProductListOnPage(stubSizeOfProductListOnPage);
    }

    @BeforeEach
    void init() {
        paginationNumbersBuilder = new PaginationNumbersBuilder(appProperties);
    }

    @Test
    void given_currentPageLowerThanOffset_when_getPaginationNumbers_return_paginationNumbersFrom1() {
        List<Integer> actualNumbers = paginationNumbersBuilder.build(pageWithLowNumber);

        assertEquals(expectedNumbersFromOne, actualNumbers);
    }

    @Test
    void given_lessPagesThanPaginationSize_when_getPaginationNumbers_return_paginationNumbersFrom1() {
        List<Integer> actualNumbers = paginationNumbersBuilder.build(pageSmaller);

        assertEquals(expectedNumbersSmallerThanSize, actualNumbers);
    }

    @Test
    void given_pageNumberCloseToTotalPages_when_getPaginationNumbers_return_paginationNumbersToTheTotalPages() {
        List<Integer> actualNumbers = paginationNumbersBuilder.build(pageWithHighNumber);

        assertEquals(expectedNumbersCloseToEnd, actualNumbers);
    }

    @Test
    void given_pageNumberInTheMiddleOfList_when_getPaginationNumbers_return_paginationNumbersWithOffset() {
        List<Integer> actualNumbers = paginationNumbersBuilder.build(expectedPage);

        assertEquals(expectedNumbersFromOffset, actualNumbers);
    }

    @Test
    void given_onePage_when_getPagniationNumbers_return_null() {
        assertNull(paginationNumbersBuilder.build(onePage));
    }

    @Test
    void given_pageNumberHigherThanTotalPages_when_getPaginationNumbers_return_null() {
        assertNull(paginationNumbersBuilder.build(pageWithTooHighNumber));
    }
}
