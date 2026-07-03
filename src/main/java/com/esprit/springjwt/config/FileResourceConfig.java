package com.esprit.springjwt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class FileResourceConfig implements WebMvcConfigurer {

    @Value("${files.folder}")
    private String filesFolder;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = Paths.get(filesFolder).toUri().toString();
        if (!location.endsWith("/")) location += "/";
        registry.addResourceHandler("/api/uploads/**")
                .addResourceLocations(location);
    }
}
