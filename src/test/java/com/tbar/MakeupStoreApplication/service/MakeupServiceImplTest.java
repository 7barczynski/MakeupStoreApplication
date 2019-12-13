package com.tbar.MakeupStoreApplication.service;

import com.tbar.MakeupStoreApplication.service.consumer.MultiAPIConsumer;
import com.tbar.MakeupStoreApplication.service.consumer.SoloAPIConsumer;
import com.tbar.MakeupStoreApplication.service.consumer.model.Item;
import com.tbar.MakeupStoreApplication.utility.exceptions.APIConnectionException;
import com.tbar.MakeupStoreApplication.utility.exceptions.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.URI;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MakeupServiceImplTest {

    // === constants ===
    private final URI STUB_BASE_URI = URI.create("http://www.example.com");
    private final Set<String> STUB_VALID_PARAMETERS = new HashSet<>(Set.of("first", "second"));
    private final String STUB_URI_SUFFIX = ".json";

    private final Item EXPECTED_ITEM = new Item();
    private final List<Item> EXPECTED_LIST = new ArrayList<>(List.of(EXPECTED_ITEM));

    private final URI URI_WITH_ID_PATH = URI.create(STUB_BASE_URI + "/1000" + STUB_URI_SUFFIX);
    private final URI URI_WITH_TWO_PARAMETERS = URI.create(STUB_BASE_URI + "?first=value&second=value2");
    private final Map<String, String> MAP_WITH_VALID_PARAMETERS = new LinkedHashMap<>(Map.of("first", "value", "second", "value2"));
    private final Map<String, String> MAP_WITH_MIXED_PARAMETERS = new LinkedHashMap<>(Map.of("first", "value", "second", "value2", "third", "value3"));
    private final Map<String, String> MAP_WITH_WRONG_PARAMETERS = new LinkedHashMap<>(Map.of("third", "value3","fourth", "value4", "fifth", "value5"));
    // === fields ===
    @Mock
    private SoloAPIConsumer soloSearchConsumerMock;
    @Mock
    private MultiAPIConsumer multiSearchConsumerMock;
    private MakeupService makeupService;

    @BeforeEach
    void initialize() {
        makeupService = new MakeupServiceImpl(multiSearchConsumerMock, soloSearchConsumerMock);
        // initialize fields that are injected from properties file
        ReflectionTestUtils.setField(makeupService, "multiBaseUri", STUB_BASE_URI);
        ReflectionTestUtils.setField(makeupService, "soloBaseUri", STUB_BASE_URI);
        ReflectionTestUtils.setField(makeupService, "soloUriSuffix", STUB_URI_SUFFIX);
        ReflectionTestUtils.setField(makeupService, "validParameters", STUB_VALID_PARAMETERS);
    }

    // === tests ===
        // === getProducts ===
    @Test
    void given_mapOfValidParameters_when_getProducts_return_listOfItems() throws APIConnectionException, ProductNotFoundException {
        when(multiSearchConsumerMock.requestData(URI_WITH_TWO_PARAMETERS)).thenReturn(new ResponseEntity<>(List.of(EXPECTED_ITEM), HttpStatus.OK));

        List<Item> actualList = makeupService.getProducts(MAP_WITH_VALID_PARAMETERS);

        assertEquals(EXPECTED_LIST, actualList);
    }

    @Test
    void given_mapOfMixedParameters_when_getProducts_return_listOfItems() throws APIConnectionException, ProductNotFoundException {
        when(multiSearchConsumerMock.requestData(URI_WITH_TWO_PARAMETERS)).thenReturn(new ResponseEntity<>(List.of(EXPECTED_ITEM), HttpStatus.OK));

        List<Item> actualList = makeupService.getProducts(MAP_WITH_MIXED_PARAMETERS);

        assertEquals(EXPECTED_LIST, actualList);
    }

    @Test
    void given_mapOfWrongParameters_when_getProducts_return_listOfItems() throws APIConnectionException, ProductNotFoundException {
        when(multiSearchConsumerMock.requestData(STUB_BASE_URI)).thenReturn(new ResponseEntity<>(List.of(EXPECTED_ITEM), HttpStatus.OK));

        List<Item> actualList = makeupService.getProducts(MAP_WITH_WRONG_PARAMETERS);

        assertEquals(EXPECTED_LIST, actualList);
    }

    @Test
    void given_nullMap_when_getProducts_return_listOfItems() throws ProductNotFoundException, APIConnectionException {
        when(multiSearchConsumerMock.requestData(STUB_BASE_URI)).thenReturn(new ResponseEntity<>(List.of(EXPECTED_ITEM), HttpStatus.OK));

        List<Item> actualList = makeupService.getProducts(null);

        assertEquals(EXPECTED_LIST, actualList);
    }

    @Test
    void given_emptyMap_when_getProducts_return_listOfItems() throws ProductNotFoundException, APIConnectionException {
        when(multiSearchConsumerMock.requestData(STUB_BASE_URI)).thenReturn(new ResponseEntity<>(List.of(EXPECTED_ITEM), HttpStatus.OK));

        List<Item> actualList = makeupService.getProducts(new HashMap<>());

        assertEquals(EXPECTED_LIST, actualList);
    }

    @Test
    void given_responseHasGotOtherStatusCodeThan200_when_getProducts_throw_APIConnectionException() {
        when(multiSearchConsumerMock.requestData(URI_WITH_TWO_PARAMETERS)).thenReturn(new ResponseEntity<>(null, HttpStatus.SERVICE_UNAVAILABLE));

        assertThrows(APIConnectionException.class, ()-> makeupService.getProducts(MAP_WITH_VALID_PARAMETERS));
    }

    @Test
    void given_responseBodyIsEmpty_when_getProducts_throw_ProductNotFoundException() {
        when(multiSearchConsumerMock.requestData(URI_WITH_TWO_PARAMETERS)).thenReturn(new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK));

        assertThrows(ProductNotFoundException.class, ()-> makeupService.getProducts(MAP_WITH_VALID_PARAMETERS));
    }

        // === getProduct ===
    @Test
    void given_properId_when_getProduct_return_Item() throws ProductNotFoundException, APIConnectionException {
        when(soloSearchConsumerMock.requestData(URI_WITH_ID_PATH)).thenReturn(new ResponseEntity<>(EXPECTED_ITEM, HttpStatus.OK));

        Item actualItem = makeupService.getProduct(1000L);

        assertEquals(EXPECTED_ITEM, actualItem);
    }

    @Test
    void given_wrongId_when_getProduct_throw_ProductNotFoundException() {
        when(soloSearchConsumerMock.requestData(URI_WITH_ID_PATH)).thenReturn(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));

        assertThrows(ProductNotFoundException.class, ()-> makeupService.getProduct(1000L));
    }

    @Test
    void given_responseHasGotOtherStatusCodeThan200Or404_when_getProduct_throw_APIConnectionException() {
        when(soloSearchConsumerMock.requestData(URI_WITH_ID_PATH)).thenReturn(new ResponseEntity<>(null, HttpStatus.SERVICE_UNAVAILABLE));

        assertThrows(APIConnectionException.class, ()-> makeupService.getProduct(1000L));
    }
}