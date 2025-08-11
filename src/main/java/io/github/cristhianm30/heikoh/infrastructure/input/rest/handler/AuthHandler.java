package io.github.cristhianm30.heikoh.infrastructure.input.rest.handler;

import io.github.cristhianm30.heikoh.application.dto.request.LoginRequest;
import io.github.cristhianm30.heikoh.application.dto.request.RegisterUserRequest;
import io.github.cristhianm30.heikoh.application.service.AuthService;
import io.github.cristhianm30.heikoh.infrastructure.util.validation.ValidateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

import static io.github.cristhianm30.heikoh.domain.util.constant.PathConstant.*;

@Component
@RequiredArgsConstructor
public class AuthHandler {

    private final AuthService authService;
    private final ValidateRequest validateRequest;

    public Mono<ServerResponse> registerUser(ServerRequest request) {
        return request.bodyToMono(RegisterUserRequest.class)
                .doOnNext(validateRequest::validate)
                .flatMap(authService::register)
                .flatMap(userResponse -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .location(URI.create(AUTH_BASE_PATH + AUTH_REGISTER_ENDPOINT_PATH + userResponse.getId()))
                        .bodyValue(userResponse));
    }

    public Mono<ServerResponse> loginUser(ServerRequest request) {
        return request.bodyToMono(LoginRequest.class)
                .doOnNext(validateRequest::validate)
                .flatMap(authService::login)
                .flatMap(loginResponse -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(loginResponse));
    }

    public Mono<ServerResponse> refreshToken(ServerRequest request) {
        return request.principal()
                .flatMap(principal -> authService.refresh(principal.getName()))
                .flatMap(loginResponse -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(loginResponse));
    }


}
