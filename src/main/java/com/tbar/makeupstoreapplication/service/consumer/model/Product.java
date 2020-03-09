package com.tbar.makeupstoreapplication.service.consumer.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {

    private Long id;
    private String brand;
    private String name;
    private String price;
    @JsonAlias(value = "price_sign")
    private String priceSign;
    private String currency;
    @JsonAlias(value = "image_link")
    private String imageLink;
    @JsonAlias(value="product_link")
    private String productLink;
    @JsonAlias(value = "website_link")
    private String websiteLink;
    private String description;
    private Double rating;
    private String category;
    @JsonAlias(value = "product_type")
    private String productType;
    @JsonAlias(value = "tag_list")
    private String[] tagList;
    @JsonAlias(value = "created_at")
    private String createdAt;
    @JsonAlias(value = "updated_at")
    private String updatedAt;
    @JsonAlias(value = "product_api_url")
    private String productApiUrl;
    @JsonAlias(value = "api_featured_image")
    private String apiFeaturedImage;
    @JsonAlias(value = "product_colors")
    private Color[] productColors;

    @Override
    public String toString() {
        return String.format("[ID:%d; Name:%s;Price:%s]", getId(), getName(), getPrice());
    }
}
