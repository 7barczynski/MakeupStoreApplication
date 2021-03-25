package com.tbar.makeupstoreapplication.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Color {

    @JsonAlias(value = "hex_value")
    private String hexValue;
    @JsonAlias(value = "colour_name")
    private String colorName;
}
