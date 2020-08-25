package com.tbar.makeupstoreapplication.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Color {

    @Id
    @GeneratedValue
    @EqualsAndHashCode.Exclude
    private int id;
    @JsonAlias(value = "hex_value")
    private String hexValue;
    @JsonAlias(value = "colour_name")
    private String colorName;
}
