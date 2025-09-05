package io.github.cristhianm30.heikoh.infrastructure.input.rest.handler;

import io.github.cristhianm30.heikoh.application.dto.request.LoginRequest;
import io.github.cristhianm30.heikoh.application.dto.request.RegisterUserRequest;
import io.github.cristhianm30.heikoh.application.dto.response.LoginResponse;
import io.github.cristhianm30.heikoh.application.dto.response.UserResponse;
import io.github.cristhianm30.heikoh.application.service.AuthService;
import io.github.cristhianm30.heikoh.infrastructure.exception.ErrorResponse;
import io.github.cristhianm30.heikoh.infrastructure.util.validation.ValidateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(
            summary = "Register a new user",
            operationId = "registerUser",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User registered successfully",
                            content = @Content(schema = @Schema(implementation = UserResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "User already exists",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            },
            requestBody = @RequestBody(
                    required = true,
                    description = "User registration data",
                    content = @Content(schema = @Schema(implementation = RegisterUserRequest.class))
            )
    )
    public Mono<ServerResponse> registerUser(ServerRequest request) {
        return request.bodyToMono(RegisterUserRequest.class)
                .doOnNext(validateRequest::validate)
                .flatMap(authService::register)
                .flatMap(userResponse -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .location(URI.create(AUTH_BASE_PATH + AUTH_REGISTER_ENDPOINT_PATH + userResponse.getId()))
                        .bodyValue(userResponse));
    }

    @Operation(
            summary = "Login a user",
            operationId = "loginUser",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User logged in successfully",
                            content = @Content(schema = @Schema(implementation = LoginResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid credentials",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "User not found",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            },
            requestBody = @RequestBody(
                    required = true,
                    description = "User login data",
                    content = @Content(schema = @Schema(implementation = LoginRequest.class))
            )
    )
    public Mono<ServerResponse> loginUser(ServerRequest request) {
        return request.bodyToMono(LoginRequest.class)
                .doOnNext(validateRequest::validate)
                .flatMap(authService::login)
                .flatMap(loginResponse -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(loginResponse));
    }

    @Operation(
            summary = "Refresh user token",
            operationId = "refreshToken",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Token refreshed successfully",
                            content = @Content(schema = @Schema(implementation = LoginResponse.class))),
                    @ApiResponse(responseCode = "404", description = "User not found",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public Mono<ServerResponse> refreshToken(ServerRequest request) {
        return request.principal()
                .flatMap(principal -> authService.refresh(principal.getName()))
                .flatMap(loginResponse -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(loginResponse));
    }


}
