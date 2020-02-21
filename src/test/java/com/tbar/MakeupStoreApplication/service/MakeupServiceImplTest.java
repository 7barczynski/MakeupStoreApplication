package com.tbar.MakeupStoreApplication.service;

import com.tbar.MakeupStoreApplication.service.consumer.ProductProductConsumer;
import com.tbar.MakeupStoreApplication.service.consumer.SoloAPIConsumer;
import com.tbar.MakeupStoreApplication.service.consumer.model.Item;
import com.tbar.MakeupStoreApplication.utility.AppProperties;
import com.tbar.MakeupStoreApplication.utility.exceptions.consumerLayer.APICallClientSideException;
import com.tbar.MakeupStoreApplication.utility.exceptions.consumerLayer.APICallNotFoundException;
import com.tbar.MakeupStoreApplication.utility.exceptions.consumerLayer.APICallServerSideException;
import com.tbar.MakeupStoreApplication.utility.exceptions.serviceLayer.APIConnectionException;
import com.tbar.MakeupStoreApplication.utility.exceptions.serviceLayer.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MakeupServiceImplTest {

    // === constants ===
    private final URI STUB_BASE_URI = URI.create("http://www.example.com");
    private final Set<String> STUB_VALID_PARAMETERS = new HashSet<>(Set.of("first", "second"));
    private final String STUB_URI_SUFFIX = ".json";
    private final int STUB_PAGINATION_SIZE = 10;
    private final int STUB_PAGINATION_OFFSET = 4;

    private final Long EXAMPLE_ID = 1000L;
    private final Item EXPECTED_ITEM = new Item();
    private final String FIRST_ENTRY_KEY = "first";
    private final String FIRST_ENTRY_VALUE = "value";
    private final String SECOND_ENTRY_KEY = "second";
    private final String SECOND_ENTRY_VALUE = "value2";
    private final URI URI_WITH_ID_PATH = URI.create(STUB_BASE_URI + "/" + EXAMPLE_ID + STUB_URI_SUFFIX);
    private final URI URI_WITH_TWO_PARAMETERS = URI.create(String.format("%s?%s=%s&%s=%s", STUB_BASE_URI, FIRST_ENTRY_KEY, FIRST_ENTRY_VALUE, SECOND_ENTRY_KEY, SECOND_ENTRY_VALUE));
    private final Map<String, String> MAP_WITH_VALID_PARAMETERS = new LinkedHashMap<>();
    private final Map<String, String> MAP_WITH_MIXED_PARAMETERS = new LinkedHashMap<>();
    private final Map<String, String> MAP_WITH_WRONG_PARAMETERS = new LinkedHashMap<>();

    private final int PAGE_ITEMS_LIST_SIZE = 9;
    private final List<Item> EXPECTED_LIST = new ArrayList<>(Collections.nCopies(STUB_PAGINATION_SIZE * PAGE_ITEMS_LIST_SIZE * 2, new Item()));
    private final List<Item> PAGE_ITEMS_LIST = Collections.nCopies(PAGE_ITEMS_LIST_SIZE, new Item());
    private final List<Item> LIST_SMALLER_THAN_PAGINATION_SIZE = new ArrayList<>(Collections.nCopies(STUB_PAGINATION_SIZE * PAGE_ITEMS_LIST_SIZE / 2, new Item()));
    private final List<Item> LIST_SMALLER_THAN_PAGE_LIST_SIZE = new ArrayList<>(Collections.nCopies(PAGE_ITEMS_LIST_SIZE / 2, new Item()));

    private final int PAGE_NUMBER_MIDDLE = STUB_PAGINATION_OFFSET * 2;
    private final int PAGE_NUMBER_SMALLER_THAN_OFFSET = STUB_PAGINATION_OFFSET / 2;
    private final int PAGE_NUMBER_CLOSE_TO_MAX = EXPECTED_LIST.size() / PAGE_ITEMS_LIST_SIZE - STUB_PAGINATION_OFFSET;

    private final PageRequest PAGE_REQUEST_MIDDLE_NUMBER = PageRequest.of(PAGE_NUMBER_MIDDLE - 1, PAGE_ITEMS_LIST_SIZE);
    private final PageRequest PAGE_REQUEST_LOW_NUMBER = PageRequest.of(PAGE_NUMBER_SMALLER_THAN_OFFSET - 1, PAGE_ITEMS_LIST_SIZE);
    private final PageRequest PAGE_REQUEST_HIGH_NUMBER = PageRequest.of(PAGE_NUMBER_CLOSE_TO_MAX - 1, PAGE_ITEMS_LIST_SIZE);
    private final PageRequest PAGE_REQUEST_TOO_HIGH_NUMBER = PageRequest.of(99999, PAGE_ITEMS_LIST_SIZE);
    private final PageRequest PAGE_REQUEST_ZERO_NUMBER = PageRequest.of(0, PAGE_ITEMS_LIST_SIZE);

    private final Page<Item> EXPECTED_PAGE = new PageImpl<>(PAGE_ITEMS_LIST, PAGE_REQUEST_MIDDLE_NUMBER, EXPECTED_LIST.size());
    private final Page<Item> PAGE_WITH_LOW_NUMBER = new PageImpl<>(PAGE_ITEMS_LIST, PAGE_REQUEST_LOW_NUMBER, EXPECTED_LIST.size());
    private final Page<Item> PAGE_WITH_HIGH_NUMBER = new PageImpl<>(PAGE_ITEMS_LIST, PAGE_REQUEST_HIGH_NUMBER, EXPECTED_LIST.size());
    private final Page<Item> PAGE_WITH_TOO_HIGH_NUMBER = new PageImpl<>(Collections.emptyList(), PAGE_REQUEST_TOO_HIGH_NUMBER, EXPECTED_LIST.size());
    private final Page<Item> PAGE_SMALLER = new PageImpl<>(PAGE_ITEMS_LIST, PAGE_REQUEST_LOW_NUMBER, LIST_SMALLER_THAN_PAGINATION_SIZE.size());
    private final Page<Item> ONE_PAGE = new PageImpl<>(LIST_SMALLER_THAN_PAGE_LIST_SIZE, PAGE_REQUEST_ZERO_NUMBER, LIST_SMALLER_THAN_PAGE_LIST_SIZE.size());

    private final List<Integer> EXPECTED_NUMBERS_FROM_ONE = IntStream.rangeClosed(1, STUB_PAGINATION_SIZE)
            .boxed()
            .collect(Collectors.toList());
    private final List<Integer> EXPECTED_NUMBERS_FROM_OFFSET = IntStream.rangeClosed(EXPECTED_PAGE.getNumber()+1-STUB_PAGINATION_OFFSET, EXPECTED_PAGE.getNumber()-STUB_PAGINATION_OFFSET+STUB_PAGINATION_SIZE)
            .boxed()
            .collect(Collectors.toList());
    private final List<Integer> EXPECTED_NUMBERS_CLOSE_TO_END = IntStream.rangeClosed(PAGE_WITH_HIGH_NUMBER.getTotalPages()+1-STUB_PAGINATION_SIZE, PAGE_WITH_HIGH_NUMBER.getTotalPages())
            .boxed()
            .collect(Collectors.toList());
    private final List<Integer> EXPECTED_NUMBERS_SMALLER_THAN_SIZE = IntStream.rangeClosed(1, PAGE_SMALLER.getTotalPages())
            .boxed()
            .collect(Collectors.toList());

    // === fields ===
    @Mock
    private SoloAPIConsumer soloSearchConsumerMock;
    @Mock
    private ProductProductConsumer multiSearchConsumerMock;
    private MakeupService makeupService;
    private AppProperties appProperties = new AppProperties();

    // === constructors ===
    MakeupServiceImplTest() {
        // initialize fields that are injected from properties file
        ReflectionTestUtils.setField(appProperties, "makeupApiMultiBaseUri", STUB_BASE_URI.toString());
        ReflectionTestUtils.setField(appProperties, "makeupApiSoloBaseUri", STUB_BASE_URI.toString());
        ReflectionTestUtils.setField(appProperties, "makeupApiSoloUriSuffix", STUB_URI_SUFFIX);
        ReflectionTestUtils.setField(appProperties, "makeupApiValidParameters", STUB_VALID_PARAMETERS.toArray(new String[0]));
        ReflectionTestUtils.setField(appProperties, "paginationNumbersSize", STUB_PAGINATION_SIZE);
        ReflectionTestUtils.setField(appProperties, "paginationLeftOffset", STUB_PAGINATION_OFFSET);

        // Putting here just to ensure proper order of entries. If wasn't tests may occasionally crush.
        MAP_WITH_VALID_PARAMETERS.put(FIRST_ENTRY_KEY, FIRST_ENTRY_VALUE);
        MAP_WITH_VALID_PARAMETERS.put(SECOND_ENTRY_KEY, SECOND_ENTRY_VALUE);

        MAP_WITH_MIXED_PARAMETERS.put(FIRST_ENTRY_KEY, FIRST_ENTRY_VALUE);
        MAP_WITH_MIXED_PARAMETERS.put(SECOND_ENTRY_KEY, SECOND_ENTRY_VALUE);
        MAP_WITH_MIXED_PARAMETERS.put("third", "value3");

        MAP_WITH_WRONG_PARAMETERS.put("third", "value3");
        MAP_WITH_WRONG_PARAMETERS.put("fourth", "value4");
    }

    // === initialization ===
    @BeforeEach
    void initialize() {
        makeupService = new MakeupServiceImpl(appProperties, multiSearchConsumerMock, soloSearchConsumerMock);
    }

    // === getProducts method ===
    @Test
    void given_mapOfValidParameters_when_getProducts_return_listOfItems() {
        when(multiSearchConsumerMock.requestData(URI_WITH_TWO_PARAMETERS)).thenReturn(new ResponseEntity<>(EXPECTED_LIST, HttpStatus.OK));

        List<Item> actualList = makeupService.getProducts(MAP_WITH_VALID_PARAMETERS);

        assertEquals(EXPECTED_LIST, actualList);
    }

    @Test
    void given_mapOfMixedParameters_when_getProducts_return_listOfItems() {
        when(multiSearchConsumerMock.requestData(URI_WITH_TWO_PARAMETERS)).thenReturn(new ResponseEntity<>(EXPECTED_LIST, HttpStatus.OK));

        List<Item> actualList = makeupService.getProducts(MAP_WITH_MIXED_PARAMETERS);

        assertEquals(EXPECTED_LIST, actualList);
    }

    @Test
    void given_mapOfWrongParameters_when_getProducts_return_listOfItems() {
        when(multiSearchConsumerMock.requestData(STUB_BASE_URI)).thenReturn(new ResponseEntity<>(EXPECTED_LIST, HttpStatus.OK));

        List<Item> actualList = makeupService.getProducts(MAP_WITH_WRONG_PARAMETERS);

        assertEquals(EXPECTED_LIST, actualList);
    }

    @Test
    void given_nullMap_when_getProducts_return_listOfItems() {
        when(multiSearchConsumerMock.requestData(STUB_BASE_URI)).thenReturn(new ResponseEntity<>(EXPECTED_LIST, HttpStatus.OK));

        List<Item> actualList = makeupService.getProducts(null);

        assertEquals(EXPECTED_LIST, actualList);
    }

    @Test
    void given_emptyMap_when_getProducts_return_listOfItems() {
        when(multiSearchConsumerMock.requestData(STUB_BASE_URI)).thenReturn(new ResponseEntity<>(EXPECTED_LIST, HttpStatus.OK));

        List<Item> actualList = makeupService.getProducts(new HashMap<>());

        assertEquals(EXPECTED_LIST, actualList);
    }

    @Test
    void given_responseThrowAPICallNotFoundException_when_getProducts_throw_ProductNotFoundException() {
        when(multiSearchConsumerMock.requestData(URI_WITH_TWO_PARAMETERS)).thenThrow(new APICallNotFoundException());

        assertThrows(ProductNotFoundException.class, ()-> makeupService.getProducts(MAP_WITH_VALID_PARAMETERS));
    }

    @Test
    void given_responseThrowAPICallServerSideException_when_getProducts_throw_APIConnectionException() {
        when(multiSearchConsumerMock.requestData(URI_WITH_TWO_PARAMETERS)).thenThrow(new APICallServerSideException(503));

        assertThrows(APIConnectionException.class, ()-> makeupService.getProducts(MAP_WITH_VALID_PARAMETERS));
    }

    @Test
    void given_responseThrowAPICallClientSideException_when_getProducts_throw_APIConnectionException() {
        when(multiSearchConsumerMock.requestData(URI_WITH_TWO_PARAMETERS)).thenThrow(new APICallClientSideException(400));

        assertThrows(APIConnectionException.class, ()-> makeupService.getProducts(MAP_WITH_VALID_PARAMETERS));
    }

    @Test
    void given_responseBodyIsEmpty_when_getProducts_throw_ProductNotFoundException() {
        when(multiSearchConsumerMock.requestData(URI_WITH_TWO_PARAMETERS)).thenReturn(new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK));

        assertThrows(ProductNotFoundException.class, ()-> makeupService.getProducts(MAP_WITH_VALID_PARAMETERS));
    }

    @Test
    void given_responseBodyIsNull_when_getProducts_throw_ProductNotFoundException() {
        when(multiSearchConsumerMock.requestData(URI_WITH_TWO_PARAMETERS)).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        assertThrows(ProductNotFoundException.class, ()-> makeupService.getProducts(MAP_WITH_VALID_PARAMETERS));
    }

    // === getProduct method ===
    @Test
    void given_properId_when_getProduct_return_Item() {
        when(soloSearchConsumerMock.requestData(URI_WITH_ID_PATH)).thenReturn(new ResponseEntity<>(EXPECTED_ITEM, HttpStatus.OK));

        Item actualItem = makeupService.getProduct(EXAMPLE_ID);

        assertEquals(EXPECTED_ITEM, actualItem);
    }

    @Test
    void given_responseThrowAPICallNotFoundException_when_getProduct_throw_ProductNotFoundException() {
        when(soloSearchConsumerMock.requestData(URI_WITH_ID_PATH)).thenThrow(new APICallNotFoundException());

        assertThrows(ProductNotFoundException.class, ()-> makeupService.getProduct(EXAMPLE_ID));
    }

    @Test
    void given_responseThrowAPICallServerSideException_when_getProduct_throw_APIConnectionException() {
        when(soloSearchConsumerMock.requestData(URI_WITH_ID_PATH)).thenThrow(new APICallServerSideException(503));

        assertThrows(APIConnectionException.class, ()-> makeupService.getProduct(EXAMPLE_ID));
    }

    @Test
    void given_responseThrowAPICallClientSideException_when_getProduct_throw_APIConnectionException() {
        when(soloSearchConsumerMock.requestData(URI_WITH_ID_PATH)).thenThrow(new APICallClientSideException(400));

        assertThrows(APIConnectionException.class, ()-> makeupService.getProduct(EXAMPLE_ID));
    }

    @Test
    void given_responseWithOkStatusAndNullBody_when_getProduct_throw_ProductNotFoundException() {
        when(soloSearchConsumerMock.requestData(URI_WITH_ID_PATH)).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        assertThrows(ProductNotFoundException.class, ()-> makeupService.getProduct(EXAMPLE_ID));
    }

    // === getPaginatedProducts ===
    @Test
    void given_validMapAndPage_when_getPaginatedProducts_return_pageOfItems() {
        when(multiSearchConsumerMock.requestData(URI_WITH_TWO_PARAMETERS)).thenReturn(new ResponseEntity<>(EXPECTED_LIST, HttpStatus.OK));

        Page<Item> actualPage = makeupService.getPaginatedProducts(MAP_WITH_VALID_PARAMETERS, PAGE_NUMBER_MIDDLE, PAGE_ITEMS_LIST_SIZE);

        assertEquals(EXPECTED_PAGE, actualPage);
    }

    @Test
    void given_nullMapAndValidPage_when_getPaginatedProducts_return_pageOfItems() {
        when(multiSearchConsumerMock.requestData(STUB_BASE_URI)).thenReturn(new ResponseEntity<>(EXPECTED_LIST, HttpStatus.OK));

        Page<Item> actualPage = makeupService.getPaginatedProducts(null, PAGE_NUMBER_MIDDLE, PAGE_ITEMS_LIST_SIZE);

        assertEquals(EXPECTED_PAGE, actualPage);
    }

    @Test
    void given_nullMapAndWrongPage_when_getPaginatedProducts_return_emptyPage() {
        when(multiSearchConsumerMock.requestData(STUB_BASE_URI)).thenReturn(new ResponseEntity<>(EXPECTED_LIST, HttpStatus.OK));

        Page<Item> actualPage = makeupService.getPaginatedProducts(null, 99999, PAGE_ITEMS_LIST_SIZE);

        assertEquals(Collections.emptyList(), actualPage.getContent());
    }

    @Test
    void given_responseThrowAPICallNotFoundException_when_getPaginatedProducts_throw_ProductNotFoundException() {
        when(multiSearchConsumerMock.requestData(URI_WITH_TWO_PARAMETERS)).thenThrow(new APICallNotFoundException());

        assertThrows(ProductNotFoundException.class, ()-> makeupService.getPaginatedProducts(MAP_WITH_VALID_PARAMETERS, PAGE_NUMBER_SMALLER_THAN_OFFSET, PAGE_ITEMS_LIST_SIZE));
    }

    @Test
    void given_responseThrowAPICallServerSideException_when_getPaginatedProducts_throw_APIConnectionException() {
        when(multiSearchConsumerMock.requestData(URI_WITH_TWO_PARAMETERS)).thenThrow(new APICallServerSideException(503));

        assertThrows(APIConnectionException.class, ()-> makeupService.getPaginatedProducts(MAP_WITH_VALID_PARAMETERS, PAGE_NUMBER_SMALLER_THAN_OFFSET, PAGE_ITEMS_LIST_SIZE));
    }

    @Test
    void given_responseThrowAPICallClientSideException_when_getPaginatedProducts_throw_APIConnectionException() {
        when(multiSearchConsumerMock.requestData(URI_WITH_TWO_PARAMETERS)).thenThrow(new APICallClientSideException(400));

        assertThrows(APIConnectionException.class, ()-> makeupService.getPaginatedProducts(MAP_WITH_VALID_PARAMETERS, PAGE_NUMBER_SMALLER_THAN_OFFSET, PAGE_ITEMS_LIST_SIZE));
    }

    @Test
    void given_responseBodyIsEmpty_when_getPaginatedProducts_throw_ProductNotFoundException() {
        when(multiSearchConsumerMock.requestData(URI_WITH_TWO_PARAMETERS)).thenReturn(new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK));

        assertThrows(ProductNotFoundException.class, ()-> makeupService.getPaginatedProducts(MAP_WITH_VALID_PARAMETERS, PAGE_NUMBER_SMALLER_THAN_OFFSET, PAGE_ITEMS_LIST_SIZE));
    }

    @Test
    void given_responseBodyIsNull_when_getPaginatedProducts_throw_ProductNotFoundException() {
        when(multiSearchConsumerMock.requestData(URI_WITH_TWO_PARAMETERS)).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        assertThrows(ProductNotFoundException.class, ()-> makeupService.getPaginatedProducts(MAP_WITH_VALID_PARAMETERS, PAGE_NUMBER_SMALLER_THAN_OFFSET, PAGE_ITEMS_LIST_SIZE));
    }

    // === getPaginationNumbers ===
    @Test
    void given_currentPageLowerThanOffset_when_getPaginationNumbers_return_paginationNumbersFrom1() {
        List<Integer> actualNumbers = makeupService.getPaginationNumbers(PAGE_WITH_LOW_NUMBER);

        assertEquals(EXPECTED_NUMBERS_FROM_ONE, actualNumbers);
    }

    @Test
    void given_lessPagesThanPaginationSize_when_getPaginationNumbers_return_paginationNumbersFrom1() {
        List<Integer> actualNumbers = makeupService.getPaginationNumbers(PAGE_SMALLER);

        assertEquals(EXPECTED_NUMBERS_SMALLER_THAN_SIZE, actualNumbers);
    }

    @Test
    void given_pageNumberCloseToTotalPages_when_getPaginationNumbers_return_paginationNumbersToTheTotalPages() {
        List<Integer> actualNumbers = makeupService.getPaginationNumbers(PAGE_WITH_HIGH_NUMBER);

        assertEquals(EXPECTED_NUMBERS_CLOSE_TO_END, actualNumbers);
    }

    @Test
    void given_pageNumberInTheMiddleOfList_when_getPaginationNumbers_return_paginationNumbersWithOffset() {
        List<Integer> actualNumbers = makeupService.getPaginationNumbers(EXPECTED_PAGE);

        assertEquals(EXPECTED_NUMBERS_FROM_OFFSET, actualNumbers);
    }

    @Test
    void given_onePage_when_getPagniationNumbers_return_null() {
        assertNull(makeupService.getPaginationNumbers(ONE_PAGE));
    }

    @Test
    void given_pageNumberHigherThanTotalPages_when_getPaginationNumbers_return_null() {
        assertNull(makeupService.getPaginationNumbers(PAGE_WITH_TOO_HIGH_NUMBER));
    }

}