//package com.tbar.makeupstoreapplication.service;
//
//import com.tbar.makeupstoreapplication.dao.MakeupAPIConsumer;
//import com.tbar.makeupstoreapplication.model.Product;
//import com.tbar.makeupstoreapplication.utility.AppProperties;
//import com.tbar.makeupstoreapplication.utility.errorhandlers.MakeupAPIErrorHandler;
//import com.tbar.makeupstoreapplication.utility.exceptions.APIConnectionException;
//import com.tbar.makeupstoreapplication.utility.exceptions.ProductsNotFoundException;
//import com.tbar.makeupstoreapplication.utility.exceptions.SingleProductNotFoundException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
//import org.springframework.boot.web.client.RestTemplateBuilder;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.client.ExpectedCount;
//import org.springframework.test.web.client.MockRestServiceServer;
//import org.springframework.test.web.client.response.MockRestResponseCreators;
//import org.springframework.web.client.RestTemplate;
//
//import java.net.URI;
//import java.util.*;
//import java.util.stream.Collectors;
//import java.util.stream.IntStream;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
//import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
//
//@RestClientTest
//class MakeupServiceIntegrationTest {
//
//    private final URI stubBaseUri = URI.create("http://www.example.com");
//    private final Set<String> stubValidParameters = new HashSet<>(Set.of("first", "second"));
//    private final String stubUriSuffix = ".json";
//    private final int stubPaginationNumbersSize = 10;
//    private final int stubPaginationOffset = 4;
//    private final int stubSizeOfProductListOnPage = 12;
//
//    private final Product expectedProduct = new Product();
//    private final List<Product> expectedList = new ArrayList<>(
//            Collections.nCopies(stubPaginationNumbersSize * stubSizeOfProductListOnPage * 2, new Product()));
//    private final List<Product> expectedListWithOneProduct = new ArrayList<>(List.of(expectedProduct));
//    private final String expectedSoloJsonResponse = "{\"id\" : \"1000\"}";
//    private final String expectedMultiJsonResponse = "[{\"id\" : \"1000\"}]";
//
//    private final Long exampleId = 1000L;
//    private final String firstEntryKey = "first";
//    private final String firstEntryValue = "value";
//    private final String secondEntryKey = "second";
//    private final String secondEntryValue = "value2";
//    private final URI uriWithIdPath = URI.create(stubBaseUri + "/" + exampleId + stubUriSuffix);
//    private final URI uriWithTwoParameters = URI.create(String.format("%s?%s=%s&%s=%s",
//            stubBaseUri, firstEntryKey, firstEntryValue, secondEntryKey, secondEntryValue));
//    private final Map<String, String> mapWithValidParameters = new LinkedHashMap<>();
//    private final Map<String, String> mapWithMixedParameters = new LinkedHashMap<>();
//    private final Map<String, String> mapWithWrongParameters = new LinkedHashMap<>();
//
//    private final List<Product> productsListOnPage = Collections.nCopies(stubSizeOfProductListOnPage, new Product());
//    private final List<Product> listSmallerThanPaginationSize = new ArrayList<>(
//            Collections.nCopies(stubPaginationNumbersSize * stubSizeOfProductListOnPage / 2, new Product()));
//    private final List<Product> listSmallerThanPageListSize = new ArrayList<>(
//            Collections.nCopies(stubSizeOfProductListOnPage / 2, new Product()));
//
//    private final int pageNumberMiddle = stubPaginationOffset * 2;
//    private final int pageNumberSmallerThanOffset = stubPaginationOffset / 2;
//    private final int pageNumberCloseToMax = expectedList.size() / stubSizeOfProductListOnPage - stubPaginationOffset;
//
//    private final PageRequest pageRequestMiddleNumber = PageRequest.of(pageNumberMiddle - 1, stubSizeOfProductListOnPage);
//    private final PageRequest pageRequestLowNumber = PageRequest.of(
//            pageNumberSmallerThanOffset - 1, stubSizeOfProductListOnPage);
//    private final PageRequest pageRequestHighNumber = PageRequest.of(pageNumberCloseToMax - 1, stubSizeOfProductListOnPage);
//    private final PageRequest pageRequestTooHighNumber = PageRequest.of(99999, stubSizeOfProductListOnPage);
//    private final PageRequest pageRequestZeroNumber = PageRequest.of(0, stubSizeOfProductListOnPage);
//
//    private final Page<Product> expectedPage = new PageImpl<>(
//            productsListOnPage, pageRequestMiddleNumber, expectedList.size());
//    private final Page<Product> pageWithLowNumber = new PageImpl<>(
//            productsListOnPage, pageRequestLowNumber, expectedList.size());
//    private final Page<Product> pageWithHighNumber = new PageImpl<>(
//            productsListOnPage, pageRequestHighNumber, expectedList.size());
//    private final Page<Product> pageWithTooHighNumber = new PageImpl<>(
//            Collections.emptyList(), pageRequestTooHighNumber, expectedList.size());
//    private final Page<Product> pageSmaller = new PageImpl<>(
//            productsListOnPage, pageRequestLowNumber, listSmallerThanPaginationSize.size());
//    private final Page<Product> onePage = new PageImpl<>(
//            listSmallerThanPageListSize, pageRequestZeroNumber, listSmallerThanPageListSize.size());
//
//    private final List<Integer> expectedNumbersFromOne = IntStream.rangeClosed(1, stubPaginationNumbersSize)
//            .boxed()
//            .collect(Collectors.toList());
//    private final List<Integer> expectedNumbersFromOffset = IntStream.rangeClosed(
//            expectedPage.getNumber()+1- stubPaginationOffset,
//            expectedPage.getNumber() - stubPaginationOffset + stubPaginationNumbersSize)
//            .boxed()
//            .collect(Collectors.toList());
//    private final List<Integer> expectedNumbersCloseToEnd = IntStream.rangeClosed(
//            pageWithHighNumber.getTotalPages()+1- stubPaginationNumbersSize, pageWithHighNumber.getTotalPages())
//            .boxed()
//            .collect(Collectors.toList());
//    private final List<Integer> expectedNumbersSmallerThanSize = IntStream.rangeClosed(1, pageSmaller.getTotalPages())
//            .boxed()
//            .collect(Collectors.toList());
//
//    @Autowired
//    private RestTemplateBuilder restTemplateBuilder;
//    private RestTemplate restTemplate;
//    private MockRestServiceServer mockRestServiceServer;
//    private MakeupAPIConsumer makeupAPIConsumer;
//    private PaginationNumbersBuilder paginationNumbersBuilder;
//    private MakeupService makeupService;
//    private AppProperties appProperties = new AppProperties();
//
//    MakeupServiceIntegrationTest() {
//        appProperties.setMakeupApiUriForCollection(stubBaseUri.toString());
//        appProperties.setMakeupApiUriForSingleObject(stubBaseUri.toString());
//        appProperties.setMakeupApiSingleObjectUriSuffix(stubUriSuffix);
//        appProperties.setMakeupApiValidParameters(stubValidParameters.toArray(new String[0]));
//        appProperties.setPaginationNumbersSize(stubPaginationNumbersSize);
//        appProperties.setPaginationLeftOffset(stubPaginationOffset);
//        appProperties.setSizeOfProductListOnPage(stubSizeOfProductListOnPage);
//
//        // ensuring order of entries that is needed to proper comparing in assertions statements.
//        mapWithValidParameters.put(firstEntryKey, firstEntryValue);
//        mapWithValidParameters.put(secondEntryKey, secondEntryValue);
//
//        mapWithMixedParameters.put(firstEntryKey, firstEntryValue);
//        mapWithMixedParameters.put(secondEntryKey, secondEntryValue);
//        mapWithMixedParameters.put("third", "value3");
//
//        mapWithWrongParameters.put("third", "value3");
//        mapWithWrongParameters.put("fourth", "value4");
//    }
//
//    @BeforeEach
//    void init() {
//        restTemplate = restTemplateBuilder.errorHandler(new MakeupAPIErrorHandler()).build();
//        mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);
//        makeupAPIConsumer = new MakeupAPIConsumer(restTemplate);
//        paginationNumbersBuilder = new PaginationNumbersBuilder(appProperties);
//        makeupService = new MakeupServiceImpl(makeupAPIConsumer, paginationNumbersBuilder, appProperties);
//        expectedProduct.setId(1000L);
//    }
//
//    @Test
//    void given_mapOfValidParameters_when_getProductCollection_return_productCollection()
//            throws ProductsNotFoundException {
//        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(uriWithTwoParameters))
//                .andExpect(method(HttpMethod.GET))
//                .andRespond(MockRestResponseCreators.withSuccess(expectedMultiJsonResponse, MediaType.APPLICATION_JSON));
//
//        List<Product> actualList = makeupService.getProductCollection(mapWithValidParameters);
//
//        assertEquals(expectedListWithOneProduct, actualList);
//    }
//
//    @Test
//    void given_mapOfMixedParameters_when_getProductCollection_return_productCollection()
//            throws ProductsNotFoundException {
//        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(uriWithTwoParameters))
//                .andExpect(method(HttpMethod.GET))
//                .andRespond(MockRestResponseCreators.withSuccess(expectedMultiJsonResponse, MediaType.APPLICATION_JSON));
//
//        List<Product> actualList = makeupService.getProductCollection(mapWithMixedParameters);
//
//        assertEquals(expectedListWithOneProduct, actualList);
//    }
//
//    @Test
//    void given_mapOfWrongParameters_when_getProductCollection_return_productCollection()
//            throws ProductsNotFoundException {
//        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(stubBaseUri))
//                .andExpect(method(HttpMethod.GET))
//                .andRespond(MockRestResponseCreators.withSuccess(expectedMultiJsonResponse, MediaType.APPLICATION_JSON));
//
//        List<Product> actualList = makeupService.getProductCollection(mapWithWrongParameters);
//
//        assertEquals(expectedListWithOneProduct, actualList);
//    }
//
//    @Test
//    void given_nullMapOfParameters_when_getProductCollection_return_productCollection()
//            throws ProductsNotFoundException {
//        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(stubBaseUri))
//                .andExpect(method(HttpMethod.GET))
//                .andRespond(MockRestResponseCreators.withSuccess(expectedMultiJsonResponse, MediaType.APPLICATION_JSON));
//
//        List<Product> actualList = makeupService.getProductCollection(null);
//
//        assertEquals(expectedListWithOneProduct, actualList);
//    }
//
//    @Test
//    void given_emptyMapOfParameters_when_getProductCollection_return_productCollection()
//            throws ProductsNotFoundException {
//        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(stubBaseUri))
//                .andExpect(method(HttpMethod.GET))
//                .andRespond(MockRestResponseCreators.withSuccess(expectedMultiJsonResponse, MediaType.APPLICATION_JSON));
//
//        List<Product> actualList = makeupService.getProductCollection(new HashMap<>());
//
//        assertEquals(expectedListWithOneProduct, actualList);
//    }
//
//    @Test
//    void given_APIRespondWithServerSideError_when_getProductCollection_throw_APIConnectionException() {
//        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(uriWithTwoParameters))
//                .andExpect(method(HttpMethod.GET))
//                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.SERVICE_UNAVAILABLE));
//
//        assertThrows(APIConnectionException.class, () -> makeupService.getProductCollection(mapWithValidParameters));
//    }
//
//    @Test
//    void given_APIRespondWithClientSideError_when_getProductCollection_throw_APIConnectionException() {
//        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(uriWithTwoParameters))
//                .andExpect(method(HttpMethod.GET))
//                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.BAD_REQUEST));
//
//        assertThrows(APIConnectionException.class, () -> makeupService.getProductCollection(mapWithValidParameters));
//    }
//
//    @Test
//    void given_responseBodyIsEmpty_when_getProductCollection_throw_ProductsNotFoundException() {
//        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(uriWithTwoParameters))
//                .andExpect(method(HttpMethod.GET))
//                .andRespond(MockRestResponseCreators.withSuccess("", MediaType.APPLICATION_JSON));
//
//        assertThrows(ProductsNotFoundException.class, () -> makeupService.getProductCollection(mapWithValidParameters));
//    }
//
//    @Test
//    void given_responseBodyIsNull_when_getProductCollection_throw_ProductsNotFoundException() {
//        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(uriWithTwoParameters))
//                .andExpect(method(HttpMethod.GET))
//                .andRespond(MockRestResponseCreators.withSuccess());
//
//        assertThrows(ProductsNotFoundException.class, () -> makeupService.getProductCollection(mapWithValidParameters));
//    }
//
//    @Test
//    void given_validId_when_getProduct_return_Product() throws SingleProductNotFoundException {
//        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(uriWithIdPath))
//                .andExpect(method(HttpMethod.GET))
//                .andRespond(MockRestResponseCreators.withSuccess(expectedSoloJsonResponse, MediaType.APPLICATION_JSON));
//
//        Product actualProduct = makeupService.getProduct(exampleId);
//
//        assertEquals(expectedProduct, actualProduct);
//    }
//
//    @Test
//    void given_APIRespondWithOKStatusAndEmptyBody_when_getProduct_throw_SingleProductNotFoundException() {
//        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(uriWithIdPath))
//                .andExpect(method(HttpMethod.GET))
//                .andRespond(MockRestResponseCreators.withSuccess("", MediaType.APPLICATION_JSON));
//
//        assertThrows(SingleProductNotFoundException.class, () -> makeupService.getProduct(exampleId));
//    }
//
//    @Test
//    void given_APIRespondWithServerSideError_when_getProduct_throw_APIConnectionException() {
//        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(uriWithIdPath))
//                .andExpect(method(HttpMethod.GET))
//                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.SERVICE_UNAVAILABLE));
//
//        assertThrows(APIConnectionException.class, () -> makeupService.getProduct(exampleId));
//    }
//
//    @Test
//    void given_APIRespondWithClientSideException_when_getProduct_throw_APIConnectionException() {
//        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(uriWithIdPath))
//                .andExpect(method(HttpMethod.GET))
//                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.BAD_REQUEST));
//
//        assertThrows(APIConnectionException.class, () -> makeupService.getProduct(exampleId));
//    }
//
//    @Test
//    void given_currentPageLowerThanOffset_when_getPaginationNumbers_return_paginationNumbersFrom1() {
//        List<Integer> actualNumbers = makeupService.getPaginationNumbers(pageWithLowNumber);
//
//        assertEquals(expectedNumbersFromOne, actualNumbers);
//    }
//
//    @Test
//    void given_lessPagesThanPaginationSize_when_getPaginationNumbers_return_paginationNumbersFrom1() {
//        List<Integer> actualNumbers = makeupService.getPaginationNumbers(pageSmaller);
//
//        assertEquals(expectedNumbersSmallerThanSize, actualNumbers);
//    }
//
//    @Test
//    void given_pageNumberCloseToTotalPages_when_getPaginationNumbers_return_paginationNumbersToTheTotalPages() {
//        List<Integer> actualNumbers = makeupService.getPaginationNumbers(pageWithHighNumber);
//
//        assertEquals(expectedNumbersCloseToEnd, actualNumbers);
//    }
//
//    @Test
//    void given_pageNumberInTheMiddleOfList_when_getPaginationNumbers_return_paginationNumbersWithOffset() {
//        List<Integer> actualNumbers = makeupService.getPaginationNumbers(expectedPage);
//
//        assertEquals(expectedNumbersFromOffset, actualNumbers);
//    }
//
//    @Test
//    void given_onePage_when_getPagniationNumbers_return_null() {
//        assertNull(makeupService.getPaginationNumbers(onePage));
//    }
//
//    @Test
//    void given_pageNumberHigherThanTotalPages_when_getPaginationNumbers_return_null() {
//        assertNull(makeupService.getPaginationNumbers(pageWithTooHighNumber));
//    }
//}