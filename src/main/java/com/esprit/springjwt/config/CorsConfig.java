package com.esprit.springjwt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.resource.ResourceResolver;
import org.springframework.web.servlet.resource.PathResourceResolver;

@Configuration
public class CorsConfig {
    
    @Value("${files.folder}")
    private String filesFolder;
    
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(
                            "http://localhost:4200",
                            "http://localhost:8094",
                            "https://9antrafrontend-production.up.railway.app"
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
            
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                // Serve static files from the uploads folder with proper caching
                registry.addResourceHandler("/uploads/**")
                        .addResourceLocations("file:" + filesFolder + "/")
                        .setCachePeriod(3600)  // Cache for 1 hour
                        .resourceChain(true)
                        .addResolver(new PathResourceResolver());
            }
        };
    }
}
