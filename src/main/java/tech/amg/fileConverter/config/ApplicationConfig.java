package tech.amg.fileConverter.config;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class ApplicationConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("http://localhost:8080") // Add your allowed origins here
                .allowedMethods("GET", "POST", "PUT", "DELETE").allowedHeaders("*");
    }
}
