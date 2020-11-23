package com.tbar.makeupstoreapplication.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {

    @Id
    private Long id;
    private String brand;
    private String name;
    private Double price;
    @JsonAlias(value = "price_sign")
    private String priceSign;
    private String currency;
    @JsonAlias(value = "image_link")
    @Column(length = 500)
    private String imageLink;
    @ToString.Exclude
    @JsonAlias(value="product_link")
    private String productLink;
    @JsonAlias(value = "website_link")
    @ToString.Exclude
    private String websiteLink;
    @ToString.Exclude
    @Column(length = 10000)
    private String description;
    private Double rating;
    private String category;
    @JsonAlias(value = "product_type")
    private String productType;
    @JsonAlias(value = "tag_list")
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "PRODUCT_ID")
    private Set<ProductTag> productTags;
    @ToString.Exclude
    @JsonAlias(value = "created_at")
    private String createdAt;
    @ToString.Exclude
    @JsonAlias(value = "updated_at")
    private String updatedAt;
    @ToString.Exclude
    @JsonAlias(value = "product_api_url")
    private String productApiUrl;
    @ToString.Exclude
    @JsonAlias(value = "api_featured_image")
    private String apiFeaturedImage;
    @JsonAlias(value = "product_colors")
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "PRODUCT_ID")
    private Set<Color> productColors;
}
