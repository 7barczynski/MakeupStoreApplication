package com.tbar.makeupstoreapplication.service;

import com.tbar.makeupstoreapplication.utility.AppProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class DAOMapCreator {

    private Set<String> makeupProductValidParameters;
    private Map<String, String> mapToBuild;

    @Autowired
    public DAOMapCreator(AppProperties appProperties) {
        this.makeupProductValidParameters = Set.of(appProperties.getMakeupProductValidParameters());
    }

    public Map<String, String> createMap(@Nullable Map<String, String> requestParameters) {
        mapToBuild = new HashMap<>();
        if (requestParameters != null) {
            putValidParametersFrom(requestParameters);
        }
        return mapToBuild;
    }

    private void putValidParametersFrom(Map<String, String> source) {
        for (Map.Entry<String, String> entry : source.entrySet()) {
            if (makeupProductValidParameters.contains(entry.getKey())) {
                mapToBuild.put(entry.getKey(), entry.getValue());
            }
        }
    }
}
