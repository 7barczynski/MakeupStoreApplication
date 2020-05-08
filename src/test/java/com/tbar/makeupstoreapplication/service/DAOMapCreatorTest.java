package com.tbar.makeupstoreapplication.service;

import com.tbar.makeupstoreapplication.utility.AppProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DAOMapCreatorTest {

    private AppProperties appProperties = new AppProperties();
    private String firstValidKey = "firstKey";
    private String secondValidKey = "secondKey";
    private String firstValidValue = "firstValue";
    private String secondValidValue = "secondValue";
    private String[] validParameters = {firstValidKey, secondValidKey};
    private Map<String, String> expectedMap = Map.of(firstValidKey, firstValidValue, secondValidKey, secondValidValue);
    private Map<String, String> expectedEmptyMap = Collections.emptyMap();
    private DAOMapCreator daoMapCreator;
    private Map<String, String> exampleMap;

    @BeforeEach
    void setUp() {
        appProperties.setMakeupProductValidParameters(validParameters);
        daoMapCreator = new DAOMapCreator(appProperties);
        exampleMap = new HashMap<>();
    }

    @Test
    void given_mapWithAllValidParameters_when_createMap_return_validMap() {
        putValidParametersToExampleMap();
        Map<String, String> actualMap = daoMapCreator.createMap(exampleMap);
        assertEquals(expectedMap, actualMap);
    }

    @Test
    void given_nullMap_when_createMap_return_emptyMap() {
        Map<String, String> actualMap = daoMapCreator.createMap(null);
        assertEquals(expectedEmptyMap, actualMap);
    }

    @Test
    void given_emptyMap_when_createMap_return_emptyMap() {
        Map<String, String> actualMap = daoMapCreator.createMap(Collections.emptyMap());
        assertEquals(expectedEmptyMap, actualMap);
    }

    @Test
    void given_mapWithMixedParameters_when_createMap_return_validMap() {
        putValidParametersToExampleMap();
        putNonValidParametersToExampleMap();
        Map<String, String> actualMap = daoMapCreator.createMap(exampleMap);
        assertEquals(expectedMap, actualMap);
    }

    @Test
    void given_mapWithWrongParameters_when_createMap_return_emptyMap() {
        putNonValidParametersToExampleMap();
        Map<String, String> actualMap = daoMapCreator.createMap(exampleMap);
        assertEquals(expectedEmptyMap, actualMap);
    }

    private void putValidParametersToExampleMap() {
        exampleMap.put(firstValidKey, firstValidValue);
        exampleMap.put(secondValidKey, secondValidValue);
    }

    private void putNonValidParametersToExampleMap() {
        exampleMap.put("nonValidKey", "anyValue");
        exampleMap.put("secondNonValidKey", "anyValue");
    }
}