package com.tbar.makeupstoreapplication.service;

import com.tbar.makeupstoreapplication.service.consumer.MakeupAPIConsumer;
import com.tbar.makeupstoreapplication.service.consumer.model.Item;
import com.tbar.makeupstoreapplication.utility.AppProperties;
import com.tbar.makeupstoreapplication.utility.errorhandlers.MakeupAPIErrorHandler;
import com.tbar.makeupstoreapplication.utility.exceptions.APIConnectionException;
import com.tbar.makeupstoreapplication.utility.exceptions.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

@RestClientTest
class MakeupServiceIntegrationTest {

    private final URI stubBaseUri = URI.create("http://www.example.com");
    private final Set<String> stubValidParameters = new HashSet<>(Set.of("first", "second"));
    private final String stubUriSuffix = ".json";
    private final int stubPaginationNumbersSize = 10;
    private final int stubPaginationOffset = 4;
    private final int stubPageItemListSize = 12;

    private final Item expectedItem = new Item();
    private final List<Item> expectedList = new ArrayList<>(
            Collections.nCopies(stubPaginationNumbersSize * stubPageItemListSize * 2, new Item()));
    private final List<Item> expectedListWithOneItem = new ArrayList<>(List.of(expectedItem));
    private final String expectedSoloJsonResponse = "{\"id\" : \"1000\"}";
    private final String expectedMultiJsonResponse = "[{\"id\" : \"1000\"}]";

    private final Long exampleId = 1000L;
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

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;
    private RestTemplate restTemplate;
    private MockRestServiceServer mockRestServiceServer;
    private MakeupAPIConsumer makeupAPIConsumer;
    private PaginationNumbersBuilder paginationNumbersBuilder;
    private MakeupService makeupService;
    private AppProperties appProperties = new AppProperties();

    MakeupServiceIntegrationTest() {
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
    void init() {
        restTemplate = restTemplateBuilder.errorHandler(new MakeupAPIErrorHandler()).build();
        mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);
        makeupAPIConsumer = new MakeupAPIConsumer(restTemplate);
        paginationNumbersBuilder = new PaginationNumbersBuilder(appProperties);
        makeupService = new MakeupServiceImpl(makeupAPIConsumer, paginationNumbersBuilder, appProperties);
        expectedItem.setId(1000L);
    }

    @Test
    void given_mapOfValidParameters_when_getProducts_return_listOfItems()
            throws ProductNotFoundException, APIConnectionException {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(uriWithTwoParameters))
                .andExpect(method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess(expectedMultiJsonResponse, MediaType.APPLICATION_JSON));

        List<Item> actualList = makeupService.getProducts(mapWithValidParameters);

        assertEquals(expectedListWithOneItem, actualList);
    }

    @Test
    void given_mapOfMixedParameters_when_getProducts_return_listOfItems()
            throws ProductNotFoundException, APIConnectionException {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(uriWithTwoParameters))
                .andExpect(method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess(expectedMultiJsonResponse, MediaType.APPLICATION_JSON));

        List<Item> actualList = makeupService.getProducts(mapWithMixedParameters);

        assertEquals(expectedListWithOneItem, actualList);
    }

    @Test
    void given_mapOfWrongParameters_when_getProducts_return_listOfItems()
            throws ProductNotFoundException, APIConnectionException {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(stubBaseUri))
                .andExpect(method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess(expectedMultiJsonResponse, MediaType.APPLICATION_JSON));

        List<Item> actualList = makeupService.getProducts(mapWithWrongParameters);

        assertEquals(expectedListWithOneItem, actualList);
    }

    @Test
    void given_nullMapOfParameters_when_getProducts_return_listOfItems()
            throws ProductNotFoundException, APIConnectionException {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(stubBaseUri))
                .andExpect(method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess(expectedMultiJsonResponse, MediaType.APPLICATION_JSON));

        List<Item> actualList = makeupService.getProducts(null);

        assertEquals(expectedListWithOneItem, actualList);
    }

    @Test
    void given_emptyMapOfParameters_when_getProducts_return_listOfItems()
            throws ProductNotFoundException, APIConnectionException {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(stubBaseUri))
                .andExpect(method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess(expectedMultiJsonResponse, MediaType.APPLICATION_JSON));

        List<Item> actualList = makeupService.getProducts(new HashMap<>());

        assertEquals(expectedListWithOneItem, actualList);
    }

    @Test
    void given_APIRespondWithServerSideError_when_getProducts_throw_APIConnectionException() {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(uriWithTwoParameters))
                .andExpect(method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.SERVICE_UNAVAILABLE));

        assertThrows(APIConnectionException.class, () -> makeupService.getProducts(mapWithValidParameters));
    }

    @Test
    void given_APIRespondWithClientSideError_when_getProducts_throw_APIConnectionException() {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(uriWithTwoParameters))
                .andExpect(method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.BAD_REQUEST));

        assertThrows(APIConnectionException.class, () -> makeupService.getProducts(mapWithValidParameters));
    }

    @Test
    void given_responseBodyIsEmpty_when_getProducts_throw_ProductNotFoundException() {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(uriWithTwoParameters))
                .andExpect(method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess("", MediaType.APPLICATION_JSON));

        assertThrows(ProductNotFoundException.class, () -> makeupService.getProducts(mapWithValidParameters));
    }

    @Test
    void given_responseBodyIsNull_when_getProducts_throw_ProductNotFoundException() {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(uriWithTwoParameters))
                .andExpect(method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess());

        assertThrows(ProductNotFoundException.class, () -> makeupService.getProducts(mapWithValidParameters));
    }

    @Test
    void given_validId_when_getProduct_return_Item() throws ProductNotFoundException, APIConnectionException {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(uriWithIdPath))
                .andExpect(method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess(expectedSoloJsonResponse, MediaType.APPLICATION_JSON));

        Item actualItem = makeupService.getProduct(exampleId);

        assertEquals(expectedItem, actualItem);
    }

    @Test
    void given_APIRespondWithOKStatusAndEmptyBody_when_getProduct_throw_ProductNotFoundException() {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(uriWithIdPath))
                .andExpect(method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess("", MediaType.APPLICATION_JSON));

        assertThrows(ProductNotFoundException.class, () -> makeupService.getProduct(exampleId));
    }

    @Test
    void given_APIRespondWithServerSideError_when_getProduct_throw_APIConnectionException() {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(uriWithIdPath))
                .andExpect(method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.SERVICE_UNAVAILABLE));

        assertThrows(APIConnectionException.class, () -> makeupService.getProduct(exampleId));
    }

    @Test
    void given_APIRespondWithClientSideException_when_getProduct_throw_APIConnectionException() {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(uriWithIdPath))
                .andExpect(method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.BAD_REQUEST));

        assertThrows(APIConnectionException.class, () -> makeupService.getProduct(exampleId));
    }

    @Test
    void given_currentPageLowerThanOffset_when_getPaginationNumbers_return_paginationNumbersFrom1() {
        List<Integer> actualNumbers = makeupService.getPaginationNumbers(pageWithLowNumber);

        assertEquals(expectedNumbersFromOne, actualNumbers);
    }

    @Test
    void given_lessPagesThanPaginationSize_when_getPaginationNumbers_return_paginationNumbersFrom1() {
        List<Integer> actualNumbers = makeupService.getPaginationNumbers(pageSmaller);

        assertEquals(expectedNumbersSmallerThanSize, actualNumbers);
    }

    @Test
    void given_pageNumberCloseToTotalPages_when_getPaginationNumbers_return_paginationNumbersToTheTotalPages() {
        List<Integer> actualNumbers = makeupService.getPaginationNumbers(pageWithHighNumber);

        assertEquals(expectedNumbersCloseToEnd, actualNumbers);
    }

    @Test
    void given_pageNumberInTheMiddleOfList_when_getPaginationNumbers_return_paginationNumbersWithOffset() {
        List<Integer> actualNumbers = makeupService.getPaginationNumbers(expectedPage);

        assertEquals(expectedNumbersFromOffset, actualNumbers);
    }

    @Test
    void given_onePage_when_getPagniationNumbers_return_null() {
        assertNull(makeupService.getPaginationNumbers(onePage));
    }

    @Test
    void given_pageNumberHigherThanTotalPages_when_getPaginationNumbers_return_null() {
        assertNull(makeupService.getPaginationNumbers(pageWithTooHighNumber));
    }
}