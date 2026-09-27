package com.insurance.policyclaims.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS = Cross-Origin Resource Sharing. Browsers block a webpage from calling
 * an API on a different "origin" (different host/port) unless the API explicitly
 * allows it. Our frontend (opened from a file, or from something like
 * http://127.0.0.1:5500 via a Live Server extension) is a different origin from
 * our backend (http://localhost:8080), so without this config, every fetch()
 * call from the frontend would be silently blocked by the browser.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
