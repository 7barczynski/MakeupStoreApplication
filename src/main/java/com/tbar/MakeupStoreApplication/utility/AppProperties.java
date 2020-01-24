package com.tbar.MakeupStoreApplication.utility;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component(value = "AppProperties")
@ConfigurationProperties(prefix = "properties")
@Getter
@Setter
public final class AppProperties {

    // === fields ===
    private String applicationName;
    private String makeupApiMultiBaseUri;
    private String makeupApiSoloBaseUri;
    private String makeupApiSoloUriSuffix;
    private String[] makeupApiValidParameters;
    private String applicationDefaultLanguage;
    private String[] applicationLanguagesList;
    private int paginationNumbersSize;
    private int paginationLeftOffset;
    private String phoneNumber;
    private String githubUrl;
    private String linkedinUrl;
    private String githubImageUrl;
    private String topHeaderLogoUrl;
    private String menuShopImageUrl;
    private String eyesImageUrl;
    private String lipsImageUrl;
    private String faceImageUrl;
}