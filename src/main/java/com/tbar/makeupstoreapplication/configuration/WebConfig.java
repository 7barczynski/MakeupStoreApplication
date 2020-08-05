package com.tbar.makeupstoreapplication.configuration;

import com.tbar.makeupstoreapplication.utility.AppProperties;
import com.tbar.makeupstoreapplication.utility.ViewNames;
import com.tbar.makeupstoreapplication.utility.interceptors.LocaleChangeInterceptor;
import com.tbar.makeupstoreapplication.utility.interceptors.LoggerInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.kaczmarzyk.spring.data.jpa.web.SpecificationArgumentResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.List;
import java.util.Locale;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AppProperties appProperties;

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
        String defaultLanguage = appProperties.getApplicationDefaultLanguage();
        sessionLocaleResolver.setDefaultLocale(Locale.forLanguageTag(defaultLanguage));
        log.debug("Setting default language of an application. Default language = {}", defaultLanguage);
        return sessionLocaleResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new SpecificationArgumentResolver());
    }

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
