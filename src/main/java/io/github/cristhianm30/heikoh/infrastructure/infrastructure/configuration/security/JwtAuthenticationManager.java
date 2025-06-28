package io.github.cristhianm30.Heikoh.infrastructure.infrastructure.configuration.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static io.github.cristhianm30.Heikoh.domain.util.constant.EnvironmentConstant.JWT_SECRET;
import static io.github.cristhianm30.Heikoh.domain.util.constant.InfrastructureConstant.ROLE;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    @Value(JWT_SECRET)
    private String secret;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();

        return Mono.just(Jwts.parser()
                        .verifyWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                        .build()
                        .parseSignedClaims(token)
                        .getPayload())
                .onErrorMap(e -> {
                    return new BadCredentialsException("Invalid or expired JWT", e);
                })
                .map(claims -> {
                    String username = claims.getSubject();
                    String role = claims.get(ROLE, String.class);
                    List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

                    return new UsernamePasswordAuthenticationToken(username, null, authorities);
                });
    }
}
