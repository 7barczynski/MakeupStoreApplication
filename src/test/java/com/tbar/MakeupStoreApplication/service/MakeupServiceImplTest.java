package com.tbar.MakeupStoreApplication.service;

import com.tbar.MakeupStoreApplication.service.consumer.APIConsumerImpl;
import com.tbar.MakeupStoreApplication.service.consumer.model.Item;
import com.tbar.MakeupStoreApplication.utility.exceptions.APIConnectionException;
import com.tbar.MakeupStoreApplication.utility.exceptions.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
    private final Item EXAMPLE_ITEM = new Item();
    private final List<Item> EXPECTED_LIST = new ArrayList<>(List.of(EXAMPLE_ITEM));

    private final URI URI_WITH_TWO_PARAMETERS = URI.create(STUB_BASE_URI + "?first=value&second=value2");
    private final Map<String, String> MAP_WITH_VALID_PARAMETERS = new HashMap<>(Map.of("first", "value", "second", "value2"));
    private final Map<String, String> MAP_WITH_MIXED_PARAMETERS = new HashMap<>(Map.of("first", "value", "second", "value2", "third", "value3"));
    private final Map<String, String> MAP_WITH_WRONG_PARAMETERS = new HashMap<>(Map.of("third", "value3","fourth", "value4", "fifth", "value5"));
    // === fields ===
    @Mock
    private APIConsumerImpl<List<Item>> apiConsumer;
    @InjectMocks
    private MakeupServiceImpl makeupService;

    @BeforeEach
    void initialize() {
        // initialize fields that are injected from properties file
        ReflectionTestUtils.setField(makeupService, "baseUri", STUB_BASE_URI);
        ReflectionTestUtils.setField(makeupService, "validParameters", STUB_VALID_PARAMETERS);
    }

    // === tests ===
    @Test
    void given_mapOfValidParameters_when_getProducts_return_listOfItems() throws APIConnectionException, ProductNotFoundException {
        when(apiConsumer.requestData(URI_WITH_TWO_PARAMETERS)).thenReturn(new ResponseEntity<>(List.of(EXAMPLE_ITEM), HttpStatus.OK));

        List<Item> actualList = makeupService.getProducts(MAP_WITH_VALID_PARAMETERS);

        assertEquals(EXPECTED_LIST, actualList);
    }

    @Test
    void given_mapOfMixedParameters_when_getProducts_return_listOfItems() throws APIConnectionException, ProductNotFoundException {
        when(apiConsumer.requestData(URI_WITH_TWO_PARAMETERS)).thenReturn(new ResponseEntity<>(List.of(EXAMPLE_ITEM), HttpStatus.OK));

        List<Item> actualList = makeupService.getProducts(MAP_WITH_MIXED_PARAMETERS);

        assertEquals(EXPECTED_LIST, actualList);
    }

    @Test
    void given_mapOfWrongParameters_when_getProducts_return_listOfItems() throws APIConnectionException, ProductNotFoundException {
        when(apiConsumer.requestData(STUB_BASE_URI)).thenReturn(new ResponseEntity<>(List.of(EXAMPLE_ITEM), HttpStatus.OK));

        List<Item> actualList = makeupService.getProducts(MAP_WITH_WRONG_PARAMETERS);

        assertEquals(EXPECTED_LIST, actualList);
    }

    @Test
    void given_nullMap_when_getProducts_return_listOfItems() throws ProductNotFoundException, APIConnectionException {
        when(apiConsumer.requestData(STUB_BASE_URI)).thenReturn(new ResponseEntity<>(List.of(EXAMPLE_ITEM), HttpStatus.OK));

        List<Item> actualList = makeupService.getProducts(null);

        assertEquals(EXPECTED_LIST, actualList);
    }

    @Test
    void given_mapOfValidParameters_when_responseHasGotOtherThan200StatusCode_in_getProducts_throw_APIConnectionException() {
        when(apiConsumer.requestData(URI_WITH_TWO_PARAMETERS)).thenReturn(new ResponseEntity<>(null, HttpStatus.SERVICE_UNAVAILABLE));

        assertThrows(APIConnectionException.class, ()-> makeupService.getProducts(MAP_WITH_VALID_PARAMETERS));
    }

    @Test
    void given_mapOfValidParameters_when_responseBodyIsEmpty_in_getProducts_throw_ProductNotFoundException() {
        when(apiConsumer.requestData(URI_WITH_TWO_PARAMETERS)).thenReturn(new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK));

        assertThrows(ProductNotFoundException.class, ()-> makeupService.getProducts(MAP_WITH_VALID_PARAMETERS));
    }
}