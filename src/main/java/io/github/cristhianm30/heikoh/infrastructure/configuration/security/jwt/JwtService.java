package io.github.cristhianm30.heikoh.infrastructure.configuration.security.jwt;

import io.github.cristhianm30.heikoh.domain.model.UserModel;
import io.github.cristhianm30.heikoh.domain.port.out.JwtPort;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static io.github.cristhianm30.heikoh.domain.util.constant.AuthConstant.ID;
import static io.github.cristhianm30.heikoh.domain.util.constant.AuthConstant.ROLE;
import static io.github.cristhianm30.heikoh.domain.util.constant.EnvironmentConstant.JWT_EXPIRATION_MINUTES;
import static io.github.cristhianm30.heikoh.domain.util.constant.EnvironmentConstant.JWT_SECRET;

@Service
public class JwtService implements JwtPort {

    @Value(JWT_SECRET)
    private String secret;

    @Value(JWT_EXPIRATION_MINUTES)
    private long expirationMinutes;

    @Override
    public Mono<String> generateToken(UserModel user) {
        return Mono.fromCallable(() -> {
            Instant now = Instant.now();
            Date expiryDate = Date.from(now.plus(expirationMinutes, ChronoUnit.MINUTES));

            return Jwts.builder()
                    .subject(user.getUsername())
                    .claim(ROLE, user.getRole())
                    .claim(ID, user.getId())
                    .issuedAt(Date.from(now))
                    .expiration(expiryDate)
                    .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                    .compact();
        });
    }
}