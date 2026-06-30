package com.esprit.springjwt.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import javax.servlet.MultipartConfigElement;

@Configuration
public class AppConfig {
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // Set the maximum allowed size (in bytes) for each individual file
        factory.setMaxFileSize(DataSize.parse("2000MB"));
        // Set the maximum allowed size (in bytes) for the entire request body
        factory.setMaxRequestSize(DataSize.parse("100MB"));
        return factory.createMultipartConfig();
    }
}
