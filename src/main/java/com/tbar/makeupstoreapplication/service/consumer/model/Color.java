package com.tbar.makeupstoreapplication.service.consumer.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Color {

    @JsonAlias(value = "hex_value")
    private String hexValue;
    @JsonAlias(value = "colour_name")
    private String colorName;
}
