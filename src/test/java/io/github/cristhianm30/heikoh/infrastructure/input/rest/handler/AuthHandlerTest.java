package io.github.cristhianm30.heikoh.infrastructure.input.rest.handler;

import io.github.cristhianm30.heikoh.application.dto.request.LoginRequest;
import io.github.cristhianm30.heikoh.application.dto.request.RegisterUserRequest;
import io.github.cristhianm30.heikoh.application.dto.response.LoginResponse;
import io.github.cristhianm30.heikoh.application.dto.response.UserResponse;
import io.github.cristhianm30.heikoh.application.service.AuthService;
import io.github.cristhianm30.heikoh.infrastructure.util.validation.ValidateRequest;
import io.github.cristhianm30.heikoh.domain.exception.RequestValidationException;
import io.github.cristhianm30.heikoh.domain.exception.UserAlreadyExistsException;
import io.github.cristhianm30.heikoh.domain.exception.InvalidPasswordException;
import io.github.cristhianm30.heikoh.domain.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

class AuthHandlerTest {

    @Mock
    private AuthService authService;

    @Mock
    private ValidateRequest validateRequest;

    @Mock
    private ServerRequest serverRequest;

    @InjectMocks
    private AuthHandler authHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_shouldReturnCreatedStatus() {
        // Given
        RegisterUserRequest registerUserRequest = new RegisterUserRequest();
        UserResponse userResponse = new UserResponse();
        userResponse.setId(1L);

        when(serverRequest.bodyToMono(RegisterUserRequest.class)).thenReturn(Mono.just(registerUserRequest));
        doNothing().when(validateRequest).validate(any(RegisterUserRequest.class));
        when(authService.register(any(RegisterUserRequest.class))).thenReturn(Mono.just(userResponse));

        // When
        Mono<ServerResponse> responseMono = authHandler.registerUser(serverRequest);

        // Then
        responseMono.subscribe(serverResponse -> {
            assert serverResponse.statusCode().equals(HttpStatus.CREATED);
            assert serverResponse.headers().getContentType().equals(MediaType.APPLICATION_JSON);
        });
    }

    @Test
    void loginUser_shouldReturnOkStatus() {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        LoginResponse loginResponse = new LoginResponse();

        when(serverRequest.bodyToMono(LoginRequest.class)).thenReturn(Mono.just(loginRequest));
        doNothing().when(validateRequest).validate(any(LoginRequest.class));
        when(authService.login(any(LoginRequest.class))).thenReturn(Mono.just(loginResponse));

        // When
        Mono<ServerResponse> responseMono = authHandler.loginUser(serverRequest);

        // Then
        responseMono.subscribe(serverResponse -> {
            assert serverResponse.statusCode().equals(HttpStatus.OK);
            assert serverResponse.headers().getContentType().equals(MediaType.APPLICATION_JSON);
        });
    }

    @Test
    void registerUser_shouldReturnBadRequest_whenValidationFails() {
        // Given
        RegisterUserRequest registerUserRequest = new RegisterUserRequest();
        when(serverRequest.bodyToMono(RegisterUserRequest.class)).thenReturn(Mono.just(registerUserRequest));
        doThrow(new RequestValidationException("Validation error")).when(validateRequest).validate(any(RegisterUserRequest.class));

        // When
        Mono<ServerResponse> responseMono = authHandler.registerUser(serverRequest);

        // Then
        responseMono.subscribe(serverResponse -> {
            assert serverResponse.statusCode().equals(HttpStatus.BAD_REQUEST);
        });
    }

    @Test
    void registerUser_shouldReturnConflict_whenUserAlreadyExists() {
        // Given
        RegisterUserRequest registerUserRequest = new RegisterUserRequest();
        when(serverRequest.bodyToMono(RegisterUserRequest.class)).thenReturn(Mono.just(registerUserRequest));
        doNothing().when(validateRequest).validate(any(RegisterUserRequest.class));
        when(authService.register(any(RegisterUserRequest.class))).thenReturn(Mono.error(new UserAlreadyExistsException("User already exists")));

        // When
        Mono<ServerResponse> responseMono = authHandler.registerUser(serverRequest);

        // Then
        responseMono.subscribe(serverResponse -> {
            assert serverResponse.statusCode().equals(HttpStatus.CONFLICT);
        });
    }

    @Test
    void loginUser_shouldReturnBadRequest_whenValidationFails() {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        when(serverRequest.bodyToMono(LoginRequest.class)).thenReturn(Mono.just(loginRequest));
        doThrow(new RequestValidationException("Validation error")).when(validateRequest).validate(any(LoginRequest.class));

        // When
        Mono<ServerResponse> responseMono = authHandler.loginUser(serverRequest);

        // Then
        responseMono.subscribe(serverResponse -> {
            assert serverResponse.statusCode().equals(HttpStatus.BAD_REQUEST);
        });
    }

    @Test
    void loginUser_shouldReturnUnauthorized_whenInvalidPassword() {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        when(serverRequest.bodyToMono(LoginRequest.class)).thenReturn(Mono.just(loginRequest));
        doNothing().when(validateRequest).validate(any(LoginRequest.class));
        when(authService.login(any(LoginRequest.class))).thenReturn(Mono.error(new InvalidPasswordException("Invalid password")));

        // When
        Mono<ServerResponse> responseMono = authHandler.loginUser(serverRequest);

        // Then
        responseMono.subscribe(serverResponse -> {
            assert serverResponse.statusCode().equals(HttpStatus.UNAUTHORIZED);
        });
    }

    @Test
    void loginUser_shouldReturnNotFound_whenUserNotFound() {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        when(serverRequest.bodyToMono(LoginRequest.class)).thenReturn(Mono.just(loginRequest));
        doNothing().when(validateRequest).validate(any(LoginRequest.class));
        when(authService.login(any(LoginRequest.class))).thenReturn(Mono.error(new UserNotFoundException("User not found")));

        // When
        Mono<ServerResponse> responseMono = authHandler.loginUser(serverRequest);

        // Then
        responseMono.subscribe(serverResponse -> {
            assert serverResponse.statusCode().equals(HttpStatus.NOT_FOUND);
        });
    }
}
