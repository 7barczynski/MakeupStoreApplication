package com.tbar.makeupstoreapplication.configuration;

import com.tbar.makeupstoreapplication.utility.AppProperties;
import com.tbar.makeupstoreapplication.utility.ViewNames;
import com.tbar.makeupstoreapplication.utility.interceptors.LocaleChangeInterceptor;
import com.tbar.makeupstoreapplication.utility.interceptors.LoggerInterceptor;
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
    private final AppProperties appProperties;

    // === constructors ===
    @Autowired
    public WebConfig(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    // === bean methods ===
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
        // Setting default language
        sessionLocaleResolver.setDefaultLocale(Locale.forLanguageTag(appProperties.getApplicationDefaultLanguage()));
        log.debug("Setting default language of an application. Default language code = {}", appProperties.getApplicationDefaultLanguage());
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

        registry.addInterceptor(new LocaleChangeInterceptor(appProperties.getApplicationLanguagesList()));
    }
}
