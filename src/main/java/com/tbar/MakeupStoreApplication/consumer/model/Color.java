package com.tbar.MakeupStoreApplication.consumer.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class Color {

    // === fields ===
    @JsonAlias(value = "hex_value")
    private String hexValue;
    @JsonAlias(value = "colour_name")
    private String colorName;

}
