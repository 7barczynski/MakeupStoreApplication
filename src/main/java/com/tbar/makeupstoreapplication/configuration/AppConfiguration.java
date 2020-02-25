package com.tbar.makeupstoreapplication.configuration;

import com.tbar.makeupstoreapplication.utility.errorhandlers.MakeupAPIErrorHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@Slf4j
public class AppConfiguration {

    // === beans ===
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        RestTemplate restTemplate = restTemplateBuilder.errorHandler(new MakeupAPIErrorHandler()).build();
        log.debug("RestTemplate bean is build by RestTemplateBuilder. RestTemplate = {}", restTemplate);
        return restTemplate;
    }
}