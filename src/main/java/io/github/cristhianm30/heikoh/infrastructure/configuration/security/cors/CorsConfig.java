package io.github.cristhianm30.heikoh.infrastructure.configuration.security.cors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static io.github.cristhianm30.heikoh.domain.util.constant.EnvironmentConstant.CORS_URL;
import static io.github.cristhianm30.heikoh.domain.util.constant.PathConstant.*;

@Configuration
public class CorsConfig {

    @Value(CORS_URL)
    private String corsUrl;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();

        corsConfig.setAllowedOrigins(List.of(corsUrl));

        corsConfig.setAllowedMethods(Arrays.asList(HTTP_GET, HTTP_POST, HTTP_PUT, HTTP_DELETE, HTTP_OPTIONS, HTTP_PATCH));

        corsConfig.setAllowedHeaders(List.of(ALLOWED_HEADERS));

        corsConfig.setExposedHeaders(List.of(AUTHORIZATION));

        corsConfig.setAllowCredentials(true);
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(ALLOWED_ALL_PATH, corsConfig);

        return new CorsWebFilter(source);
    }
}