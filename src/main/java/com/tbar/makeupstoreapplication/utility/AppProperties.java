package com.tbar.makeupstoreapplication.utility;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component(value = "AppProperties")
@ConfigurationProperties(prefix = "properties")
@Getter
@Setter
public final class AppProperties {

    private String applicationName;
    private String makeupApiUriForCollection;
    private String makeupApiUriForSingleObject;
    private String makeupApiSingleObjectUriSuffix;
    private String[] makeupApiValidParameters;
    private String applicationDefaultLanguage;
    private String[] applicationLanguagesList;
    private int paginationNumbersSize;
    private int paginationLeftOffset;
    private int pageItemListSize;
    private String phoneNumber;
    private String githubUrl;
    private String linkedinUrl;
    private String githubImageUrl;
    private String topHeaderLogoUrl;
    private String menuShopImageUrl;
    private String eyesImageUrl;
    private String lipsImageUrl;
    private String faceImageUrl;
    private String noPhotoImageUrl;
    private String[] productTypeList;
    private String[] productCategoryList;
    private String[] brandList;
    private String[] productTagsList;
}