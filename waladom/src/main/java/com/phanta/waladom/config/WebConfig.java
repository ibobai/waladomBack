package com.phanta.waladom.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public WebMvcConfigurer corsConfigurer()  {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("https://zp1v56uxy8rdx5ypatb0ockcb9tr6a-oci3--5173--d20a0a75.local-credentialless.webcontainer-api.io","http://localhost:5173")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("Authorization", "Content-Type") // Allow Authorization header
                        .exposedHeaders("Authorization") // Ensure the frontend can read it
                        .allowCredentials(true);
            }
        };
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
                .defaultContentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .favorParameter(false)   // Disable query parameter-based content negotiation
                .ignoreAcceptHeader(false) // Respect the Accept header, reject if not JSON
                .mediaType("json", org.springframework.http.MediaType.APPLICATION_JSON);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
