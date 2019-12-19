package com.tbar.MakeupStoreApplication.configuration;

import com.tbar.MakeupStoreApplication.utility.AppResources;
import com.tbar.MakeupStoreApplication.utility.ViewNames;
import com.tbar.MakeupStoreApplication.utility.interceptors.LocaleChangeInterceptor;
import com.tbar.MakeupStoreApplication.utility.interceptors.LoggerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    // === fields ===
    private final AppResources appResources;

    // === constructors ===
    @Autowired
    public WebConfig(AppResources appResources) {
        this.appResources = appResources;
    }

    // === bean methods ===
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
        // Setting default language
        sessionLocaleResolver.setDefaultLocale(Locale.forLanguageTag(appResources.APP_DEFAULT_LANGUAGE));
        log.debug("Setting default language of an application. Default language code = {}", appResources.APP_DEFAULT_LANGUAGE);
        return sessionLocaleResolver;
    }

    // === configuration methods ===
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName(ViewNames.HOME);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoggerInterceptor());

        registry.addInterceptor(new LocaleChangeInterceptor(appResources.APP_LANGUAGES_LIST));
    }
}
