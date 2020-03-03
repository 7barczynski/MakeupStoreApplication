package com.tbar.makeupstoreapplication.service;

import com.tbar.makeupstoreapplication.utility.AppProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.tbar.makeupstoreapplication.service.PaginationNumbersBuilder.CurrentPagePositionCase.*;

@Component
@Slf4j
public class PaginationNumbersBuilder {

    private int paginationNumbersSize;
    private int paginationLeftOffset;
    private int totalPages;
    private int number;
    private int currentPageMinusOffset;
    private int totalPagesMinusSize;
    private int offsetNumberPlusSize;
    private int firstNumber;
    private int lastNumber;

    enum CurrentPagePositionCase {
        AT_THE_START,
        AT_THE_END,
        IN_THE_MIDDLE
    }

    @Autowired
    public PaginationNumbersBuilder(AppProperties appProperties) {
        paginationNumbersSize = appProperties.getPaginationNumbersSize();
        paginationLeftOffset = appProperties.getPaginationLeftOffset();
    }

    public List<Integer> build(Page currentPage) {
        setupVariables(currentPage);
        if (isPaginationNeeded()) {
            return buildList();
        }
        return null;
    }

    private void setupVariables(Page currentPage) {
        totalPages = currentPage.getTotalPages();
        number = currentPage.getNumber();
        currentPageMinusOffset = number - paginationLeftOffset + 1;
        totalPagesMinusSize = totalPages - paginationNumbersSize + 1;
        offsetNumberPlusSize = currentPageMinusOffset + paginationNumbersSize - 1;
    }

    private boolean isPaginationNeeded() {
        return totalPages > 1 && number < totalPages;
    }

    private List<Integer> buildList() {
        calculateBorderIndices();
        return createListFromIndices();
    }

    private void calculateBorderIndices() {
        CurrentPagePositionCase currentPagePositionCase = choosePositionCase();
        switch (currentPagePositionCase) {
            case AT_THE_START:
                firstNumber = 1;
                lastNumber = Math.min(paginationNumbersSize, totalPages);
                break;
            case AT_THE_END:
                firstNumber = totalPagesMinusSize;
                lastNumber = totalPages;
                break;
            case IN_THE_MIDDLE:
                firstNumber = currentPageMinusOffset;
                lastNumber = offsetNumberPlusSize;
                break;
        }
    }

    private CurrentPagePositionCase choosePositionCase() {
        if (currentPageMinusOffset <= 1 || totalPagesMinusSize <= 1) {
            return AT_THE_START;
        } else if (offsetNumberPlusSize >= totalPages) {
            return AT_THE_END;
        } else {
            return IN_THE_MIDDLE;
        }
    }

    private List<Integer> createListFromIndices() {
        return IntStream.rangeClosed(firstNumber, lastNumber).boxed().collect(Collectors.toList());
    }
}
