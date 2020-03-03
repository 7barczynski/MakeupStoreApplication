package com.tbar.makeupstoreapplication.service;

import com.tbar.makeupstoreapplication.service.consumer.MakeupAPIConsumer;
import com.tbar.makeupstoreapplication.service.consumer.model.Item;
import com.tbar.makeupstoreapplication.utility.AppProperties;
import com.tbar.makeupstoreapplication.utility.exceptions.APIConnectionException;
import com.tbar.makeupstoreapplication.utility.exceptions.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MakeupServiceImplTest {

    private final URI stubBaseUri = URI.create("http://www.example.com");
    private final Set<String> stubValidParameters = new HashSet<>(Set.of("first", "second"));
    private final String stubUriSuffix = ".json";
    private final int stubPaginationNumbersSize = 10;
    private final int stubPaginationOffset = 4;
    private final int stubPageItemListSize = 12;

    private final Long exampleId = 1000L;
    private final Item expectedItem = new Item();
    private final String firstEntryKey = "first";
    private final String firstEntryValue = "value";
    private final String secondEntryKey = "second";
    private final String secondEntryValue = "value2";
    private final URI uriWithIdPath = URI.create(stubBaseUri + "/" + exampleId + stubUriSuffix);
    private final URI uriWithTwoParameters = URI.create(String.format("%s?%s=%s&%s=%s",
            stubBaseUri, firstEntryKey, firstEntryValue, secondEntryKey, secondEntryValue));
    private final Map<String, String> mapWithValidParameters = new LinkedHashMap<>();
    private final Map<String, String> mapWithMixedParameters = new LinkedHashMap<>();
    private final Map<String, String> mapWithWrongParameters = new LinkedHashMap<>();

    private final List<Item> expectedList = new ArrayList<>(
            Collections.nCopies(stubPaginationNumbersSize * stubPageItemListSize * 2, new Item()));
    private final List<Item> pageItemsList = Collections.nCopies(stubPageItemListSize, new Item());

    private final int pageNumberMiddle = stubPaginationOffset * 2;
    private final int pageNumberSmallerThanOffset = stubPaginationOffset / 2;
    private final int pageNumberCloseToMax = expectedList.size() / stubPageItemListSize - stubPaginationOffset;

    private final PageRequest pageRequestMiddleNumber = PageRequest.of(pageNumberMiddle - 1, stubPageItemListSize);

    private final Page<Item> expectedPage = new PageImpl<>(pageItemsList, pageRequestMiddleNumber, expectedList.size());

    private final List<Integer> expectedNumbersFromOne = IntStream.rangeClosed(1, stubPaginationNumbersSize)
            .boxed()
            .collect(Collectors.toList());

    @Mock
    private MakeupAPIConsumer makeupApiConsumerMock;
    @Mock
    private PaginationNumbersBuilder paginationNumbersBuilderMock;
    private MakeupService makeupService;
    private AppProperties appProperties = new AppProperties();

    MakeupServiceImplTest() {
        // initialize fields that are injected from properties file
        ReflectionTestUtils.setField(appProperties, "makeupApiUriForCollection", stubBaseUri.toString());
        ReflectionTestUtils.setField(appProperties, "makeupApiUriForSingleObject", stubBaseUri.toString());
        ReflectionTestUtils.setField(appProperties, "makeupApiSingleObjectUriSuffix", stubUriSuffix);
        ReflectionTestUtils.setField(appProperties, "makeupApiValidParameters",
                stubValidParameters.toArray(new String[0]));
        ReflectionTestUtils.setField(appProperties, "paginationNumbersSize", stubPaginationNumbersSize);
        ReflectionTestUtils.setField(appProperties, "paginationLeftOffset", stubPaginationOffset);
        ReflectionTestUtils.setField(appProperties, "pageItemListSize", stubPageItemListSize);

        // Putting here just to ensure proper order of entries. If wasn't tests may occasionally crush.
        mapWithValidParameters.put(firstEntryKey, firstEntryValue);
        mapWithValidParameters.put(secondEntryKey, secondEntryValue);

        mapWithMixedParameters.put(firstEntryKey, firstEntryValue);
        mapWithMixedParameters.put(secondEntryKey, secondEntryValue);
        mapWithMixedParameters.put("third", "value3");

        mapWithWrongParameters.put("third", "value3");
        mapWithWrongParameters.put("fourth", "value4");
    }

    @BeforeEach
    void initialize() {
        makeupService = new MakeupServiceImpl(makeupApiConsumerMock, paginationNumbersBuilderMock, appProperties);
    }

    @Test
    void given_mapOfValidParameters_when_getProducts_return_listOfItems() 
            throws ProductNotFoundException, APIConnectionException {
        when(makeupApiConsumerMock.requestCollection(uriWithTwoParameters)).thenReturn(expectedList);

        List<Item> actualList = makeupService.getProducts(mapWithValidParameters);

        assertEquals(expectedList, actualList);
    }

    @Test
    void given_mapOfMixedParameters_when_getProducts_return_listOfItems() 
            throws ProductNotFoundException, APIConnectionException {
        when(makeupApiConsumerMock.requestCollection(uriWithTwoParameters)).thenReturn(expectedList);

        List<Item> actualList = makeupService.getProducts(mapWithMixedParameters);

        assertEquals(expectedList, actualList);
    }

    @Test
    void given_mapOfWrongParameters_when_getProducts_return_listOfItems() 
            throws ProductNotFoundException, APIConnectionException {
        when(makeupApiConsumerMock.requestCollection(stubBaseUri)).thenReturn(expectedList);

        List<Item> actualList = makeupService.getProducts(mapWithWrongParameters);

        assertEquals(expectedList, actualList);
    }

    @Test
    void given_nullMap_when_getProducts_return_listOfItems() 
            throws ProductNotFoundException, APIConnectionException {
        when(makeupApiConsumerMock.requestCollection(stubBaseUri)).thenReturn(expectedList);

        List<Item> actualList = makeupService.getProducts(null);

        assertEquals(expectedList, actualList);
    }

    @Test
    void given_emptyMap_when_getProducts_return_listOfItems() 
            throws ProductNotFoundException, APIConnectionException {
        when(makeupApiConsumerMock.requestCollection(stubBaseUri)).thenReturn(expectedList);

        List<Item> actualList = makeupService.getProducts(new HashMap<>());

        assertEquals(expectedList, actualList);
    }

    @Test
    void given_responseThrowAPIConnectionException_when_getProducts_throw_APIConnectionException() {
        when(makeupApiConsumerMock.requestCollection(uriWithTwoParameters)).thenThrow(new APIConnectionException(""));

        assertThrows(APIConnectionException.class, ()-> makeupService.getProducts(mapWithValidParameters));
    }

    @Test
    void given_responseIsEmpty_when_getProducts_throw_ProductNotFoundException() {
        when(makeupApiConsumerMock.requestCollection(uriWithTwoParameters)).thenReturn(Collections.emptyList());

        assertThrows(ProductNotFoundException.class, ()-> makeupService.getProducts(mapWithValidParameters));
    }

    @Test
    void given_responseIsNull_when_getProducts_throw_ProductNotFoundException() {
        when(makeupApiConsumerMock.requestCollection(uriWithTwoParameters)).thenReturn(null);

        assertThrows(ProductNotFoundException.class, ()-> makeupService.getProducts(mapWithValidParameters));
    }

    @Test
    void given_properId_when_getProduct_return_Item() throws ProductNotFoundException, APIConnectionException {
        when(makeupApiConsumerMock.requestSingleObject(uriWithIdPath)).thenReturn(expectedItem);

        Item actualItem = makeupService.getProduct(exampleId);

        assertEquals(expectedItem, actualItem);
    }

    @Test
    void given_responseThrowAPIConnectionException_when_getProduct_throw_APIConnectionException() {
        when(makeupApiConsumerMock.requestSingleObject(uriWithIdPath)).thenThrow(new APIConnectionException(""));

        assertThrows(APIConnectionException.class, ()-> makeupService.getProduct(exampleId));
    }

    @Test
    void given_responseIsNull_when_getProduct_throw_ProductNotFoundException() {
        when(makeupApiConsumerMock.requestSingleObject(uriWithIdPath)).thenReturn(null);

        assertThrows(ProductNotFoundException.class, ()-> makeupService.getProduct(exampleId));
    }

    @Test
    void given_validMapAndPage_when_getPaginatedProducts_return_pageOfItems() 
            throws ProductNotFoundException, APIConnectionException {
        when(makeupApiConsumerMock.requestCollection(uriWithTwoParameters)).thenReturn(expectedList);

        Page<Item> actualPage = makeupService.getPaginatedProducts(mapWithValidParameters, pageNumberMiddle);

        assertEquals(expectedPage, actualPage);
    }

    @Test
    void given_nullMapAndValidPage_when_getPaginatedProducts_return_pageOfItems() 
            throws ProductNotFoundException, APIConnectionException {
        when(makeupApiConsumerMock.requestCollection(stubBaseUri)).thenReturn(expectedList);

        Page<Item> actualPage = makeupService.getPaginatedProducts(null, pageNumberMiddle);

        assertEquals(expectedPage, actualPage);
    }

    @Test
    void given_nullMapAndWrongPage_when_getPaginatedProducts_return_emptyPage() 
            throws ProductNotFoundException, APIConnectionException {
        when(makeupApiConsumerMock.requestCollection(stubBaseUri)).thenReturn(expectedList);

        Page<Item> actualPage = makeupService.getPaginatedProducts(null, 99999);

        assertEquals(Collections.emptyList(), actualPage.getContent());
    }

    @Test
    void given_responseThrowAPIConnectionException_when_getPaginatedProducts_throw_APIConnectionException() {
        when(makeupApiConsumerMock.requestCollection(uriWithTwoParameters)).thenThrow(new APIConnectionException(""));

        assertThrows(APIConnectionException.class, ()-> makeupService.getPaginatedProducts(
                mapWithValidParameters, pageNumberSmallerThanOffset));
    }

    @Test
    void given_responseIsEmpty_when_getPaginatedProducts_throw_ProductNotFoundException() {
        when(makeupApiConsumerMock.requestCollection(uriWithTwoParameters)).thenReturn(new ArrayList<>());

        assertThrows(ProductNotFoundException.class, ()-> makeupService.getPaginatedProducts(
                mapWithValidParameters, pageNumberSmallerThanOffset));
    }

    @Test
    void given_responseIsNull_when_getPaginatedProducts_throw_ProductNotFoundException() {
        when(makeupApiConsumerMock.requestCollection(uriWithTwoParameters)).thenReturn(null);

        assertThrows(ProductNotFoundException.class, ()-> makeupService.getPaginatedProducts(
                mapWithValidParameters, pageNumberSmallerThanOffset));
    }

    @Test
    void given_page_when_getPaginationNumbers_return_paginationNumbers() {
        when(paginationNumbersBuilderMock.build(expectedPage)).thenReturn(expectedNumbersFromOne);

        List<Integer> actualNumbers = makeupService.getPaginationNumbers(expectedPage);

        assertEquals(expectedNumbersFromOne, actualNumbers);
    }
}