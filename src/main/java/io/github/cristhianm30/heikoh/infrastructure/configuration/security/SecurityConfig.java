package io.github.cristhianm30.heikoh.infrastructure.configuration.security;

import io.github.cristhianm30.heikoh.infrastructure.configuration.security.jwt.JwtAuthenticationConverter;
import io.github.cristhianm30.heikoh.infrastructure.configuration.security.jwt.JwtAuthenticationManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.AndServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;

import static io.github.cristhianm30.heikoh.domain.util.constant.PathConstant.*;

@Configuration
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationManager jwtAuthenticationManager;
    private final JwtAuthenticationConverter jwtAuthenticationConverter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(HttpMethod.POST, USER_LOGIN, USER_REGISTER).permitAll()
                        .pathMatchers(COMPLETE_ACTUATOR).permitAll()
                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public AuthenticationWebFilter jwtWebFilter() {
        AuthenticationWebFilter filter = new AuthenticationWebFilter(jwtAuthenticationManager);
        filter.setServerAuthenticationConverter(jwtAuthenticationConverter);
        filter.setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance());

        AndServerWebExchangeMatcher publicMatchers = getAndServerWebExchangeMatcher();

        NegatedServerWebExchangeMatcher protectedPathsMatcher = new NegatedServerWebExchangeMatcher(publicMatchers);

        filter.setRequiresAuthenticationMatcher(protectedPathsMatcher);

        return filter;
    }


    private static AndServerWebExchangeMatcher getAndServerWebExchangeMatcher() {
        PathPatternParserServerWebExchangeMatcher loginMatcher = new PathPatternParserServerWebExchangeMatcher(USER_LOGIN, HttpMethod.POST);
        PathPatternParserServerWebExchangeMatcher registerMatcher = new PathPatternParserServerWebExchangeMatcher(USER_REGISTER, HttpMethod.POST);
        PathPatternParserServerWebExchangeMatcher actuatorMatcher = new PathPatternParserServerWebExchangeMatcher(COMPLETE_ACTUATOR);

        return new AndServerWebExchangeMatcher(loginMatcher, registerMatcher, actuatorMatcher);
    }


}