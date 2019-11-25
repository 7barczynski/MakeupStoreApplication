package com.tbar.MakeupStoreApplication.configuration;

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
        log.debug("RestTemplate is created by RestTemplateBuilder. RestTemplate = {}", restTemplateBuilder.build());
        return restTemplateBuilder.build();
    }
}