package com.tbar.MakeupStoreApplication.utility;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public final class AppResources {

    // === fields ===
    public final String APP_NAME;
    public final String APP_DEFAULT_LANGUAGE;
    public final String[] APP_LANGUAGES_LIST;
    public final String PHONE_NUMBER;
    public final String GITHUB_URL;
    public final String GITHUB_IMAGE;
    public final String TOP_HEADER_LOGO;
    public final String MENU_SHOP_IMAGE;
    public final String EYES_CATEGORY_IMAGE;
    public final String LIPS_CATEGORY_IMAGE;
    public final String FACE_CATEGORY_IMAGE;

    // === constructor ===
    public AppResources(@Value("${application.name}") String APP_NAME,
                        @Value("${application.default.language}") String APP_DEFAULT_LANGUAGE,
                        @Value("${application.languages.list}")String[] APP_LANGUAGES_LIST,
                        @Value("${resources.phone.number}") String PHONE_NUMBER,
                        @Value("${resources.home.github.url}") String GITHUB_URL,
                        @Value("${resources.home.github.image}") String GITHUB_IMAGE,
                        @Value("${resources.top.header.logo}") String TOP_HEADER_LOGO,
                        @Value("${resources.menu.shop.image}") String MENU_SHOP_IMAGE,
                        @Value("${resources.categories.eyes.image}") String EYES_CATEGORY_IMAGE,
                        @Value("${resources.categories.lips.image}") String LIPS_CATEGORY_IMAGE,
                        @Value("${resources.categories.face.image}")String FACE_CATEGORY_IMAGE) {
        this.APP_NAME = APP_NAME;
        this.APP_DEFAULT_LANGUAGE = APP_DEFAULT_LANGUAGE;
        this.APP_LANGUAGES_LIST = APP_LANGUAGES_LIST;
        this.PHONE_NUMBER = PHONE_NUMBER;
        this.GITHUB_URL = GITHUB_URL;
        this.GITHUB_IMAGE = GITHUB_IMAGE;
        this.TOP_HEADER_LOGO = TOP_HEADER_LOGO;
        this.MENU_SHOP_IMAGE = MENU_SHOP_IMAGE;
        this.EYES_CATEGORY_IMAGE = EYES_CATEGORY_IMAGE;
        this.LIPS_CATEGORY_IMAGE = LIPS_CATEGORY_IMAGE;
        this.FACE_CATEGORY_IMAGE = FACE_CATEGORY_IMAGE;
    }
}