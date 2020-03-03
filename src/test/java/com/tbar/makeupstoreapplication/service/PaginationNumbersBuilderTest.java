package com.tbar.makeupstoreapplication.service;

import com.tbar.makeupstoreapplication.service.consumer.model.Item;
import com.tbar.makeupstoreapplication.utility.AppProperties;
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

    private final int stubPaginationNumbersSize = 10;
    private final int stubPaginationOffset = 4;
    private final int stubPageItemListSize = 12;

    private final List<Item> expectedList = new ArrayList<>(
            Collections.nCopies(stubPaginationNumbersSize * stubPageItemListSize * 2, new Item()));
    private final List<Item> pageItemsList = Collections.nCopies(stubPageItemListSize, new Item());
    private final List<Item> listSmallerThanPaginationSize = new ArrayList<>(
            Collections.nCopies(stubPaginationNumbersSize * stubPageItemListSize / 2, new Item()));
    private final List<Item> listSmallerThanPageListSize = new ArrayList<>(
            Collections.nCopies(stubPageItemListSize / 2, new Item()));

    private final int pageNumberMiddle = stubPaginationOffset * 2;
    private final int pageNumberSmallerThanOffset = stubPaginationOffset / 2;
    private final int pageNumberCloseToMax = expectedList.size() / stubPageItemListSize - stubPaginationOffset;

    private final PageRequest pageRequestMiddleNumber = PageRequest.of(pageNumberMiddle - 1, stubPageItemListSize);
    private final PageRequest pageRequestLowNumber = PageRequest.of(
            pageNumberSmallerThanOffset - 1, stubPageItemListSize);
    private final PageRequest pageRequestHighNumber = PageRequest.of(pageNumberCloseToMax - 1, stubPageItemListSize);
    private final PageRequest pageRequestTooHighNumber = PageRequest.of(99999, stubPageItemListSize);
    private final PageRequest pageRequestZeroNumber = PageRequest.of(0, stubPageItemListSize);

    private final Page<Item> expectedPage = new PageImpl<>(
            pageItemsList, pageRequestMiddleNumber, expectedList.size());
    private final Page<Item> pageWithLowNumber = new PageImpl<>(
            pageItemsList, pageRequestLowNumber, expectedList.size());
    private final Page<Item> pageWithHighNumber = new PageImpl<>(
            pageItemsList, pageRequestHighNumber, expectedList.size());
    private final Page<Item> pageWithTooHighNumber = new PageImpl<>(
            Collections.emptyList(), pageRequestTooHighNumber, expectedList.size());
    private final Page<Item> pageSmaller = new PageImpl<>(
            pageItemsList, pageRequestLowNumber, listSmallerThanPaginationSize.size());
    private final Page<Item> onePage = new PageImpl<>(
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
        // initialize fields that are injected from properties file
        ReflectionTestUtils.setField(appProperties, "paginationNumbersSize", stubPaginationNumbersSize);
        ReflectionTestUtils.setField(appProperties, "paginationLeftOffset", stubPaginationOffset);
        ReflectionTestUtils.setField(appProperties, "pageItemListSize", stubPageItemListSize);
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
