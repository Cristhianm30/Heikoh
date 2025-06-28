package io.github.cristhianm30.heikoh.infrastructure.infrastructure.input.rest.handler;

import io.github.cristhianm30.heikoh.application.dto.request.RegisterUserRequest;
import io.github.cristhianm30.heikoh.application.service.RegisterUserService;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.stream.Collectors;

import static io.github.cristhianm30.heikoh.domain.util.constant.PathConstant.USER_REGISTER;

@Component
@RequiredArgsConstructor
public class RegisterUserHandler {

    private final RegisterUserService registerUserService;
    private final Validator validator;

    public Mono<ServerResponse> registerUser(ServerRequest request) {
        return request.bodyToMono(RegisterUserRequest.class)
                .doOnNext(this::validate)
                .flatMap(registerUserService::register)
                .flatMap(userResponse -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .location(URI.create(USER_REGISTER + userResponse.getId()))
                        .bodyValue(userResponse));
    }

    private <T> void validate(T object) {
        var violations = validator.validate(object);
        if (!violations.isEmpty()) {
            String errorMessages = violations.stream()
                    .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                    .collect(Collectors.joining(", "));
            throw new IllegalArgumentException("Validation failed: " + errorMessages);
        }
    }
}
