package com.tbar.MakeupStoreApplication.service;

import com.tbar.MakeupStoreApplication.service.consumer.ProductProductConsumer;
import com.tbar.MakeupStoreApplication.service.consumer.SoloAPIConsumer;
import com.tbar.MakeupStoreApplication.service.consumer.model.Item;
import com.tbar.MakeupStoreApplication.utility.AppProperties;
import com.tbar.MakeupStoreApplication.utility.exceptions.errorHandlers.MakeupAPIErrorHandler;
import com.tbar.MakeupStoreApplication.utility.exceptions.serviceLayer.APIConnectionException;
import com.tbar.MakeupStoreApplication.utility.exceptions.serviceLayer.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

@RestClientTest
class MakeupServiceIntegrationTest {

    // === constants ===
    private final URI STUB_BASE_URI = URI.create("http://www.example.com");
    private final Set<String> STUB_VALID_PARAMETERS = new HashSet<>(Set.of("first", "second"));
    private final String STUB_URI_SUFFIX = ".json";

    private final Item EXPECTED_ITEM = new Item();
    private final List<Item> EXPECTED_LIST = new ArrayList<>(List.of(EXPECTED_ITEM));
    private final String EXPECTED_SOLO_JSON_RESPONSE = "{\"id\" : \"1000\"}";
    private final String EXPECTED_MULTI_JSON_RESPONSE = "[{\"id\" : \"1000\"}]";

    private final Long EXAMPLE_ID = 1000L;
    private final String FIRST_ENTRY_KEY = "first";
    private final String FIRST_ENTRY_VALUE = "value";
    private final String SECOND_ENTRY_KEY = "second";
    private final String SECOND_ENTRY_VALUE = "value2";
    private final URI URI_WITH_ID_PATH = URI.create(STUB_BASE_URI + "/" + EXAMPLE_ID + STUB_URI_SUFFIX);
    private final URI URI_WITH_TWO_PARAMETERS = URI.create(String.format("%s?%s=%s&%s=%s", STUB_BASE_URI, FIRST_ENTRY_KEY, FIRST_ENTRY_VALUE, SECOND_ENTRY_KEY, SECOND_ENTRY_VALUE));
    private final Map<String, String> MAP_WITH_VALID_PARAMETERS = new LinkedHashMap<>();
    private final Map<String, String> MAP_WITH_MIXED_PARAMETERS = new LinkedHashMap<>();
    private final Map<String, String> MAP_WITH_WRONG_PARAMETERS = new LinkedHashMap<>();

    // === fields ==
    @Autowired
    private RestTemplateBuilder restTemplateBuilder;
    private RestTemplate restTemplate;
    private MockRestServiceServer mockRestServiceServer;
    private SoloAPIConsumer soloAPIConsumer;
    private ProductProductConsumer productAPIConsumer;
    private MakeupService makeupService;
    private AppProperties appProperties = new AppProperties();

    // === constructors ===
    MakeupServiceIntegrationTest() {
        // initialize fields that are injected from properties file
        ReflectionTestUtils.setField(appProperties, "makeupApiMultiBaseUri", STUB_BASE_URI.toString());
        ReflectionTestUtils.setField(appProperties, "makeupApiSoloBaseUri", STUB_BASE_URI.toString());
        ReflectionTestUtils.setField(appProperties, "makeupApiSoloUriSuffix", STUB_URI_SUFFIX);
        ReflectionTestUtils.setField(appProperties, "makeupApiValidParameters", STUB_VALID_PARAMETERS.toArray(new String[0]));

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
    void init() {
        restTemplate = restTemplateBuilder.errorHandler(new MakeupAPIErrorHandler()).build();
        mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);
        soloAPIConsumer = new SoloAPIConsumer(restTemplate);
        productAPIConsumer = new ProductProductConsumer(restTemplate);
        makeupService = new MakeupServiceImpl(appProperties, productAPIConsumer, soloAPIConsumer);
        EXPECTED_ITEM.setId(1000L);
    }

    // === getProducts method ===
    @Test
    void given_mapOfValidParameters_when_getProducts_return_listOfItems() {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(URI_WITH_TWO_PARAMETERS))
                .andExpect(method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess(EXPECTED_MULTI_JSON_RESPONSE, MediaType.APPLICATION_JSON));

        List<Item> actualList = makeupService.getProducts(MAP_WITH_VALID_PARAMETERS);

        assertEquals(EXPECTED_LIST, actualList);
    }

    @Test
    void given_mapOfMixedParameters_when_getProducts_return_listOfItems() {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(URI_WITH_TWO_PARAMETERS))
                .andExpect(method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess(EXPECTED_MULTI_JSON_RESPONSE, MediaType.APPLICATION_JSON));

        List<Item> actualList = makeupService.getProducts(MAP_WITH_MIXED_PARAMETERS);

        assertEquals(EXPECTED_LIST, actualList);
    }

    @Test
    void given_mapOfWrongParameters_when_getProducts_return_listOfItems() {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(STUB_BASE_URI))
                .andExpect(method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess(EXPECTED_MULTI_JSON_RESPONSE, MediaType.APPLICATION_JSON));

        List<Item> actualList = makeupService.getProducts(MAP_WITH_WRONG_PARAMETERS);

        assertEquals(EXPECTED_LIST, actualList);
    }

    @Test
    void given_nullMapOfParameters_when_getProducts_return_listOfItems() {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(STUB_BASE_URI))
                .andExpect(method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess(EXPECTED_MULTI_JSON_RESPONSE, MediaType.APPLICATION_JSON));

        List<Item> actualList = makeupService.getProducts(null);

        assertEquals(EXPECTED_LIST, actualList);
    }

    @Test
    void given_emptyMapOfParameters_when_getProducts_return_listOfItems() {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(STUB_BASE_URI))
                .andExpect(method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess(EXPECTED_MULTI_JSON_RESPONSE, MediaType.APPLICATION_JSON));

        List<Item> actualList = makeupService.getProducts(new HashMap<>());

        assertEquals(EXPECTED_LIST, actualList);
    }

    @Test
    void given_APIRespondWithNotFoundStatus_when_getProducts_throw_ProductNotFoundException() {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(URI_WITH_TWO_PARAMETERS))
                .andExpect(method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.NOT_FOUND));

        assertThrows(ProductNotFoundException.class, () -> makeupService.getProducts(MAP_WITH_VALID_PARAMETERS));
    }

    @Test
    void given_APIRespondWithServerSideError_when_getProducts_throw_APIConnectionException() {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(URI_WITH_TWO_PARAMETERS))
                .andExpect(method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.SERVICE_UNAVAILABLE));

        assertThrows(APIConnectionException.class, () -> makeupService.getProducts(MAP_WITH_VALID_PARAMETERS));
    }

    @Test
    void given_APIRespondWithClientSideError_when_getProducts_throw_APIConnectionException() {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(URI_WITH_TWO_PARAMETERS))
                .andExpect(method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.BAD_REQUEST));

        assertThrows(APIConnectionException.class, () -> makeupService.getProducts(MAP_WITH_VALID_PARAMETERS));
    }

    @Test
    void given_responseBodyIsEmpty_when_getProducts_throw_ProductNotFoundException() {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(URI_WITH_TWO_PARAMETERS))
                .andExpect(method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess("", MediaType.APPLICATION_JSON));

        assertThrows(ProductNotFoundException.class, () -> makeupService.getProducts(MAP_WITH_VALID_PARAMETERS));
    }

    @Test
    void given_responseBodyIsNull_when_getProducts_throw_ProductNotFoundException() {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(URI_WITH_TWO_PARAMETERS))
                .andExpect(method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess());

        assertThrows(ProductNotFoundException.class, () -> makeupService.getProducts(MAP_WITH_VALID_PARAMETERS));
    }

    // === getProduct method ===
    @Test
    void given_validId_when_getProduct_return_Item() {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(URI_WITH_ID_PATH))
                .andExpect(method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess(EXPECTED_SOLO_JSON_RESPONSE, MediaType.APPLICATION_JSON));

        Item actualItem = makeupService.getProduct(EXAMPLE_ID);

        assertEquals(EXPECTED_ITEM, actualItem);
    }

    @Test
    void given_wrongId_when_getProduct_throw_ProductNotFoundException() {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(URI_WITH_ID_PATH))
                .andExpect(method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.NOT_FOUND));

        assertThrows(ProductNotFoundException.class, () -> makeupService.getProduct(EXAMPLE_ID));
    }

    @Test
    void given_APIRespondWithOKStatusAndEmptyBody_when_getProduct_throw_ProductNotFoundException() {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(URI_WITH_ID_PATH))
                .andExpect(method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess("", MediaType.APPLICATION_JSON));

        assertThrows(ProductNotFoundException.class, () -> makeupService.getProduct(EXAMPLE_ID));
    }

    @Test
    void given_APIRespondWithServerSideError_when_getProduct_throw_APIConnectionException() {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(URI_WITH_ID_PATH))
                .andExpect(method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.SERVICE_UNAVAILABLE));

        assertThrows(APIConnectionException.class, () -> makeupService.getProduct(EXAMPLE_ID));
    }

    @Test
    void given_APIRespondWithClientSideException_when_getProduct_throw_APIConnectionException() {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(URI_WITH_ID_PATH))
                .andExpect(method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.BAD_REQUEST));

        assertThrows(APIConnectionException.class, () -> makeupService.getProduct(EXAMPLE_ID));
    }
}