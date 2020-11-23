package com.tbar.makeupstoreapplication.service;

import com.tbar.makeupstoreapplication.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PaginationNumbersBuilderTest {

    private final int paginationNumbersSize = 10;
    private final int paginationOffset = 4;
    private final int sizeOfProductListOnPage = 12;

    private final List<Product> exampleListOfAllProducts = new ArrayList<>(
            Collections.nCopies(paginationNumbersSize * sizeOfProductListOnPage * 2, new Product()));
    private final List<Product> regularSliceContent = Collections.nCopies(sizeOfProductListOnPage, new Product());
    private final List<Product> sliceContentSmallerThanPaginationSize = new ArrayList<>(
            Collections.nCopies(paginationNumbersSize * sizeOfProductListOnPage / 2, new Product()));
    private final List<Product> sliceContentSmallerThanPageListSize = new ArrayList<>(
            Collections.nCopies(sizeOfProductListOnPage / 2, new Product()));

    private PaginationNumbersBuilder paginationNumbersBuilder;
    private Page<Product> examplePage;
    private int examplePageNumber;
    private List<Integer> expectedNumbers;

    @BeforeEach
    void init() {
        paginationNumbersBuilder = new PaginationNumbersBuilder();
        ReflectionTestUtils.setField(paginationNumbersBuilder, "paginationNumbersSize", 10);
        ReflectionTestUtils.setField(paginationNumbersBuilder, "paginationLeftOffset", 4);
    }

    @Test
    void given_currentPageLowerThanOffset_when_getPaginationNumbers_then_returnPaginationNumbersFromOne() {
        examplePageNumber = paginationOffset / 2;
        examplePage = new PageImpl<>(regularSliceContent, PageRequest.of(
                examplePageNumber - 1, sizeOfProductListOnPage), exampleListOfAllProducts.size());
        expectedNumbers = getExpectedNumbers(1, paginationNumbersSize);
        List<Integer> actualNumbers = paginationNumbersBuilder.build(examplePage);

        assertEquals(expectedNumbers, actualNumbers);
    }

    @Test
    void given_lessPagesThanPaginationSize_when_getPaginationNumbers_then_returnPaginationNumbersFromOne() {
        examplePageNumber = paginationOffset / 2;
        examplePage = new PageImpl<>(sliceContentSmallerThanPaginationSize, PageRequest.of(
                examplePageNumber - 1, sizeOfProductListOnPage), sliceContentSmallerThanPaginationSize.size());
        expectedNumbers = getExpectedNumbers(1, examplePage.getTotalPages());
        List<Integer> actualNumbers = paginationNumbersBuilder.build(examplePage);

        assertEquals(expectedNumbers, actualNumbers);
    }

    @Test
    void given_pageNumberCloseToTotalPages_when_getPaginationNumbers_then_returnPaginationNumbersToTheTotalPages() {
        examplePageNumber = exampleListOfAllProducts.size() / sizeOfProductListOnPage - 1;
        examplePage = new PageImpl<>(regularSliceContent, PageRequest.of(
                examplePageNumber, sizeOfProductListOnPage), exampleListOfAllProducts.size());
        expectedNumbers = getExpectedNumbers(examplePage.getTotalPages() - paginationNumbersSize + 1,
                exampleListOfAllProducts.size() / sizeOfProductListOnPage);
        List<Integer> actualNumbers = paginationNumbersBuilder.build(examplePage);

        assertEquals(expectedNumbers, actualNumbers);
    }

    @Test
    void given_pageNumberInTheMiddleOfList_when_getPaginationNumbers_then_returnPaginationNumbersWithOffset() {
        examplePageNumber = paginationOffset * 2;
        examplePage = new PageImpl<>(regularSliceContent, PageRequest.of(
                examplePageNumber, sizeOfProductListOnPage), exampleListOfAllProducts.size());
        int tempFrom = examplePageNumber - paginationOffset + 1;
        expectedNumbers = getExpectedNumbers(tempFrom, tempFrom + paginationNumbersSize - 1);
        List<Integer> actualNumbers = paginationNumbersBuilder.build(examplePage);

        assertEquals(expectedNumbers, actualNumbers);
    }

    @Test
    void given_onePage_when_getPagniationNumbers_then_returnNull() {
        examplePage = new PageImpl<>(sliceContentSmallerThanPageListSize,
                PageRequest.of(0, sizeOfProductListOnPage), sliceContentSmallerThanPageListSize.size());

        assertNull(paginationNumbersBuilder.build(examplePage));
    }

    @Test
    void given_pageNumberHigherThanTotalPages_when_getPaginationNumbers_then_returnNull() {
        examplePage = new PageImpl<>(Collections.emptyList(),
                PageRequest.of(99999, sizeOfProductListOnPage), exampleListOfAllProducts.size());

        assertNull(paginationNumbersBuilder.build(examplePage));
    }

    private List<Integer> getExpectedNumbers(int from, int to) {
        return IntStream.rangeClosed(from, to).boxed().collect(Collectors.toList());
    }
}
