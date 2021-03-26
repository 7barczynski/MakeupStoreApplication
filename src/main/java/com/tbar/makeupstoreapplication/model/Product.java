package com.tbar.makeupstoreapplication.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Optional;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class Product {

    @Id
    private Long id;
    private String brand;
    private String name;
    @Setter(AccessLevel.NONE)
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
    @Setter(AccessLevel.NONE)
    private Double rating;
    private String category;
    @JsonAlias(value = "product_type")
    private String productType;
    @JsonAlias(value = "tag_list")
    @ElementCollection
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
    @ElementCollection
    private Set<Color> productColors;

    public void setPrice(Double price) {
        this.price = Optional.ofNullable(price).orElse(0.0);
    }

    public void setRating(Double rating) {
        this.rating = Optional.ofNullable(rating).orElse(0.0);
    }
}
