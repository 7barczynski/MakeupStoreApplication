package com.tbar.makeupstoreapplication.service;

import com.tbar.makeupstoreapplication.service.consumer.MakeupAPIConsumer;
import com.tbar.makeupstoreapplication.service.consumer.model.Product;
import com.tbar.makeupstoreapplication.utility.AppProperties;
import com.tbar.makeupstoreapplication.utility.exceptions.APIConnectionException;
import com.tbar.makeupstoreapplication.utility.exceptions.ProductsNotFoundException;
import com.tbar.makeupstoreapplication.utility.exceptions.SingleProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MakeupServiceImplTest {

    private final URI stubBaseUri = URI.create("http://www.example.com");
    private final Set<String> stubValidParameters = new HashSet<>(Set.of("first", "second"));
    private final String stubUriSuffix = ".json";
    private final int stubPaginationNumbersSize = 10;
    private final int stubPaginationOffset = 4;
    private final int stubSizeOfProductListOnPage = 12;

    private final Long exampleId = 1000L;
    private final Product expectedProduct = new Product();
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

    private final List<Product> expectedList = new ArrayList<>(
            Collections.nCopies(stubPaginationNumbersSize * stubSizeOfProductListOnPage * 2, new Product()));
    private final List<Product> productListOnPage = Collections.nCopies(stubSizeOfProductListOnPage, new Product());

    private final int pageNumberMiddle = stubPaginationOffset * 2;
    private final int pageNumberSmallerThanOffset = stubPaginationOffset / 2;
    private final int pageNumberCloseToMax = expectedList.size() / stubSizeOfProductListOnPage - stubPaginationOffset;

    private final PageRequest pageRequestMiddleNumber = PageRequest.of(pageNumberMiddle - 1, stubSizeOfProductListOnPage);

    private final Page<Product> expectedPage = new PageImpl<>(productListOnPage, pageRequestMiddleNumber, expectedList.size());

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
        appProperties.setMakeupApiUriForCollection(stubBaseUri.toString());
        appProperties.setMakeupApiUriForSingleObject(stubBaseUri.toString());
        appProperties.setMakeupApiSingleObjectUriSuffix(stubUriSuffix);
        appProperties.setMakeupApiValidParameters(stubValidParameters.toArray(new String[0]));
        appProperties.setPaginationNumbersSize(stubPaginationNumbersSize);
        appProperties.setPaginationLeftOffset(stubPaginationOffset);
        appProperties.setSizeOfProductListOnPage(stubSizeOfProductListOnPage);

        // ensuring order of entries that is needed to proper comparing in assertions statements.
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
    void given_mapOfValidParameters_when_getProductCollection_return_productCollection()
            throws ProductsNotFoundException {
        when(makeupApiConsumerMock.requestCollection(uriWithTwoParameters)).thenReturn(expectedList);

        List<Product> actualList = makeupService.getProductCollection(mapWithValidParameters);

        assertEquals(expectedList, actualList);
    }

    @Test
    void given_mapOfMixedParameters_when_getProductCollection_return_productCollection()
            throws ProductsNotFoundException {
        when(makeupApiConsumerMock.requestCollection(uriWithTwoParameters)).thenReturn(expectedList);

        List<Product> actualList = makeupService.getProductCollection(mapWithMixedParameters);

        assertEquals(expectedList, actualList);
    }

    @Test
    void given_mapOfWrongParameters_when_getProductCollection_return_productCollection()
            throws ProductsNotFoundException {
        when(makeupApiConsumerMock.requestCollection(stubBaseUri)).thenReturn(expectedList);

        List<Product> actualList = makeupService.getProductCollection(mapWithWrongParameters);

        assertEquals(expectedList, actualList);
    }

    @Test
    void given_nullMap_when_getProductCollection_return_productCollection() throws ProductsNotFoundException {
        when(makeupApiConsumerMock.requestCollection(stubBaseUri)).thenReturn(expectedList);

        List<Product> actualList = makeupService.getProductCollection(null);

        assertEquals(expectedList, actualList);
    }

    @Test
    void given_emptyMap_when_getProductCollection_return_productCollection() throws ProductsNotFoundException {
        when(makeupApiConsumerMock.requestCollection(stubBaseUri)).thenReturn(expectedList);

        List<Product> actualList = makeupService.getProductCollection(new HashMap<>());

        assertEquals(expectedList, actualList);
    }

    @Test
    void given_responseThrowAPIConnectionException_when_getProductCollection_throw_APIConnectionException() {
        when(makeupApiConsumerMock.requestCollection(uriWithTwoParameters)).thenThrow(new APIConnectionException(""));

        assertThrows(APIConnectionException.class, ()-> makeupService.getProductCollection(mapWithValidParameters));
    }

    @Test
    void given_responseIsEmpty_when_getProductCollection_throw_ProductsNotFoundException() {
        when(makeupApiConsumerMock.requestCollection(uriWithTwoParameters)).thenReturn(Collections.emptyList());

        assertThrows(ProductsNotFoundException.class, ()-> makeupService.getProductCollection(mapWithValidParameters));
    }

    @Test
    void given_responseIsNull_when_getProductCollection_throw_ProductsNotFoundException() {
        when(makeupApiConsumerMock.requestCollection(uriWithTwoParameters)).thenReturn(null);

        assertThrows(ProductsNotFoundException.class, ()-> makeupService.getProductCollection(mapWithValidParameters));
    }

    @Test
    void given_properId_when_getProduct_return_Product() throws SingleProductNotFoundException {
        when(makeupApiConsumerMock.requestSingleObject(uriWithIdPath)).thenReturn(expectedProduct);

        Product actualProduct = makeupService.getProduct(exampleId);

        assertEquals(expectedProduct, actualProduct);
    }

    @Test
    void given_responseThrowAPIConnectionException_when_getProduct_throw_APIConnectionException() {
        when(makeupApiConsumerMock.requestSingleObject(uriWithIdPath)).thenThrow(new APIConnectionException(""));

        assertThrows(APIConnectionException.class, ()-> makeupService.getProduct(exampleId));
    }

    @Test
    void given_responseIsNull_when_getProduct_throw_SingleProductNotFoundException() {
        when(makeupApiConsumerMock.requestSingleObject(uriWithIdPath)).thenReturn(null);

        assertThrows(SingleProductNotFoundException.class, ()-> makeupService.getProduct(exampleId));
    }

    @Test
    void given_validMapAndPage_when_getPaginatedProducts_return_pageOfProducts() throws ProductsNotFoundException {
        when(makeupApiConsumerMock.requestCollection(uriWithTwoParameters)).thenReturn(expectedList);

        Page<Product> actualPage = makeupService.getPaginatedProducts(mapWithValidParameters, pageNumberMiddle);

        assertEquals(expectedPage, actualPage);
    }

    @Test
    void given_nullMapAndValidPage_when_getPaginatedProducts_return_pageOfProducts() throws ProductsNotFoundException {
        when(makeupApiConsumerMock.requestCollection(stubBaseUri)).thenReturn(expectedList);

        Page<Product> actualPage = makeupService.getPaginatedProducts(null, pageNumberMiddle);

        assertEquals(expectedPage, actualPage);
    }

    @Test
    void given_nullMapAndWrongPage_when_getPaginatedProducts_return_emptyPage() throws ProductsNotFoundException {
        when(makeupApiConsumerMock.requestCollection(stubBaseUri)).thenReturn(expectedList);

        Page<Product> actualPage = makeupService.getPaginatedProducts(null, 99999);

        assertEquals(Collections.emptyList(), actualPage.getContent());
    }

    @Test
    void given_responseThrowAPIConnectionException_when_getPaginatedProducts_throw_APIConnectionException() {
        when(makeupApiConsumerMock.requestCollection(uriWithTwoParameters)).thenThrow(new APIConnectionException(""));

        assertThrows(APIConnectionException.class, ()-> makeupService.getPaginatedProducts(
                mapWithValidParameters, pageNumberSmallerThanOffset));
    }

    @Test
    void given_responseIsEmpty_when_getPaginatedProducts_throw_ProductsNotFoundException() {
        when(makeupApiConsumerMock.requestCollection(uriWithTwoParameters)).thenReturn(new ArrayList<>());

        assertThrows(ProductsNotFoundException.class, ()-> makeupService.getPaginatedProducts(
                mapWithValidParameters, pageNumberSmallerThanOffset));
    }

    @Test
    void given_responseIsNull_when_getPaginatedProducts_throw_ProductsNotFoundException() {
        when(makeupApiConsumerMock.requestCollection(uriWithTwoParameters)).thenReturn(null);

        assertThrows(ProductsNotFoundException.class, ()-> makeupService.getPaginatedProducts(
                mapWithValidParameters, pageNumberSmallerThanOffset));
    }

    @Test
    void given_page_when_getPaginationNumbers_return_paginationNumbers() {
        when(paginationNumbersBuilderMock.build(expectedPage)).thenReturn(expectedNumbersFromOne);

        List<Integer> actualNumbers = makeupService.getPaginationNumbers(expectedPage);

        assertEquals(expectedNumbersFromOne, actualNumbers);
    }
}