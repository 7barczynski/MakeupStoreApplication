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

    private int paginationNumbersSize = 10;
    private int paginationOffset = 4;
    private int sizeOfProductListOnPage = 12;

    private List<Product> exampleListOfAllProducts = new ArrayList<>(
            Collections.nCopies(paginationNumbersSize * sizeOfProductListOnPage * 2, new Product()));
    private List<Product> regularSliceContent = Collections.nCopies(sizeOfProductListOnPage, new Product());
    private List<Product> sliceContentSmallerThanPaginationSize = new ArrayList<>(
            Collections.nCopies(paginationNumbersSize * sizeOfProductListOnPage / 2, new Product()));
    private List<Product> sliceContentSmallerThanPageListSize = new ArrayList<>(
            Collections.nCopies(sizeOfProductListOnPage / 2, new Product()));

    private AppProperties appProperties = new AppProperties();
    private PaginationNumbersBuilder paginationNumbersBuilder;
    private Page<Product> examplePage;
    private int examplePageNumber;
    private List<Integer> expectedNumbers;

    PaginationNumbersBuilderTest() {
        appProperties.setPaginationNumbersSize(paginationNumbersSize);
        appProperties.setPaginationLeftOffset(paginationOffset);
        appProperties.setSizeOfProductListOnPage(sizeOfProductListOnPage);
    }

    @BeforeEach
    void init() {
        paginationNumbersBuilder = new PaginationNumbersBuilder(appProperties);
    }

    @Test
    void given_currentPageLowerThanOffset_when_getPaginationNumbers_return_paginationNumbersFromOne() {
        examplePageNumber = paginationOffset / 2;
        examplePage = new PageImpl<>(regularSliceContent, PageRequest.of(
                examplePageNumber - 1, sizeOfProductListOnPage), exampleListOfAllProducts.size());
        expectedNumbers = getExpectedNumbers(1, paginationNumbersSize);

        List<Integer> actualNumbers = paginationNumbersBuilder.build(examplePage);
        assertEquals(expectedNumbers, actualNumbers);
    }


    @Test
    void given_lessPagesThanPaginationSize_when_getPaginationNumbers_return_paginationNumbersFromOne() {
        examplePageNumber = paginationOffset / 2;
        examplePage = new PageImpl<>(sliceContentSmallerThanPaginationSize, PageRequest.of(
                examplePageNumber - 1, sizeOfProductListOnPage), sliceContentSmallerThanPaginationSize.size());
        expectedNumbers = getExpectedNumbers(1, examplePage.getTotalPages());

        List<Integer> actualNumbers = paginationNumbersBuilder.build(examplePage);
        assertEquals(expectedNumbers, actualNumbers);
    }

    @Test
    void given_pageNumberCloseToTotalPages_when_getPaginationNumbers_return_paginationNumbersToTheTotalPages() {
        examplePageNumber = exampleListOfAllProducts.size() / sizeOfProductListOnPage - 1;
        examplePage = new PageImpl<>(regularSliceContent, PageRequest.of(
                examplePageNumber, sizeOfProductListOnPage), exampleListOfAllProducts.size());
        expectedNumbers = getExpectedNumbers(examplePage.getTotalPages() - paginationNumbersSize + 1,
                exampleListOfAllProducts.size() / sizeOfProductListOnPage);

        List<Integer> actualNumbers = paginationNumbersBuilder.build(examplePage);
        assertEquals(expectedNumbers, actualNumbers);
    }

    @Test
    void given_pageNumberInTheMiddleOfList_when_getPaginationNumbers_return_paginationNumbersWithOffset() {
        examplePageNumber = paginationOffset * 2;
        examplePage = new PageImpl<>(regularSliceContent, PageRequest.of(
                examplePageNumber, sizeOfProductListOnPage), exampleListOfAllProducts.size());
        int tempFrom = examplePageNumber - paginationOffset + 1;
        expectedNumbers = getExpectedNumbers(tempFrom, tempFrom + paginationNumbersSize - 1);

        List<Integer> actualNumbers = paginationNumbersBuilder.build(examplePage);
        assertEquals(expectedNumbers, actualNumbers);
    }

    @Test
    void given_onePage_when_getPagniationNumbers_return_null() {
        examplePage = new PageImpl<>(sliceContentSmallerThanPageListSize,
                PageRequest.of(0, sizeOfProductListOnPage), sliceContentSmallerThanPageListSize.size());

        assertNull(paginationNumbersBuilder.build(examplePage));
    }

    @Test
    void given_pageNumberHigherThanTotalPages_when_getPaginationNumbers_return_null() {
        examplePage = new PageImpl<>(Collections.emptyList(),
                PageRequest.of(99999, sizeOfProductListOnPage), exampleListOfAllProducts.size());

        assertNull(paginationNumbersBuilder.build(examplePage));
    }

    private List<Integer> getExpectedNumbers(int from, int to) {
        return IntStream.rangeClosed(from, to).boxed().collect(Collectors.toList());
    }
}
