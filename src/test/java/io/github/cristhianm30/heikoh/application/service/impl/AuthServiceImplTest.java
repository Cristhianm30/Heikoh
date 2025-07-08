package io.github.cristhianm30.heikoh.application.service.impl;

import io.github.cristhianm30.heikoh.application.dto.request.LoginRequest;
import io.github.cristhianm30.heikoh.application.dto.request.RegisterUserRequest;
import io.github.cristhianm30.heikoh.application.dto.response.LoginResponse;
import io.github.cristhianm30.heikoh.application.dto.response.UserResponse;
import io.github.cristhianm30.heikoh.application.mapper.AuthDtoMapper;
import io.github.cristhianm30.heikoh.application.mapper.UserDtoMapper;
import io.github.cristhianm30.heikoh.domain.exception.InvalidPasswordException;
import io.github.cristhianm30.heikoh.domain.exception.UserNotEnabledException;
import io.github.cristhianm30.heikoh.domain.exception.UserNotFoundException;
import io.github.cristhianm30.heikoh.domain.model.LoginData;
import io.github.cristhianm30.heikoh.domain.model.UserModel;
import io.github.cristhianm30.heikoh.domain.port.in.AuthServicePort;
import io.github.cristhianm30.heikoh.domain.port.in.UserServicePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static io.github.cristhianm30.heikoh.domain.util.constant.ApplicationConstant.DEFAULT_USER_ROLE;
import static io.github.cristhianm30.heikoh.domain.util.constant.AuthConstant.INVALID_PASSWORD;
import static io.github.cristhianm30.heikoh.domain.util.constant.ExceptionConstants.ACCOUNT_IS_DISABLED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthServicePort authServicePort;

    @Mock
    private UserDtoMapper userDtoMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserServicePort userServicePort;

    @Mock
    private AuthDtoMapper authDtoMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterUserRequest registerRequest;
    private LoginRequest loginRequest;
    private UserModel userModel;
    private UserResponse userResponse;
    private LoginResponse loginResponse;
    private LoginData loginData;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterUserRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");

        userModel = UserModel.builder()
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .enabled(true)
                .role(DEFAULT_USER_ROLE)
                .build();

        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setUsername("testuser");
        userResponse.setEmail("test@example.com");
        userResponse.setEnabled(true);
        userResponse.setRole(DEFAULT_USER_ROLE);

        loginData = LoginData.builder()
                .token("testToken")
                .username("testuser")
                .email("test@example.com")
                .enabled(true)
                .role(DEFAULT_USER_ROLE)
                .id(1L)
                .build();

        loginResponse = new LoginResponse();
        loginResponse.setId(1L);
        loginResponse.setUsername("testuser");
        loginResponse.setEmail("test@example.com");
        loginResponse.setEnabled(true);
        loginResponse.setRole(DEFAULT_USER_ROLE);
        loginResponse.setToken("testToken");
    }

    @Test
    void register_ShouldReturnUserResponse() {
        when(userDtoMapper.toUserDomain(any(RegisterUserRequest.class))).thenReturn(userModel);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(authServicePort.registerUser(any(UserModel.class))).thenReturn(Mono.just(userModel));
        when(userDtoMapper.toUserResponse(any(UserModel.class))).thenReturn(userResponse);

        StepVerifier.create(authService.register(registerRequest))
                .expectNextMatches(response ->
                        response.getId() == 1L &&
                                "testuser".equals(response.getUsername()))
                .verifyComplete();
    }

    @Test
    void login_WithValidCredentials_ShouldReturnLoginResponse() {
        when(userServicePort.findByUsername(anyString())).thenReturn(Mono.just(userModel));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(authServicePort.loginUser(any(UserModel.class))).thenReturn(Mono.just(loginData));
        when(authDtoMapper.toLoginResponse(any(LoginData.class))).thenReturn(loginResponse);

        StepVerifier.create(authService.login(loginRequest))
                .expectNextMatches(response ->
                        "testToken".equals(response.getToken()) &&
                                "testuser".equals(response.getUsername()))
                .verifyComplete();
    }

    @Test
    void login_WithInvalidPassword_ShouldThrowException() {
        when(userServicePort.findByUsername(anyString())).thenReturn(Mono.just(userModel));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        StepVerifier.create(authService.login(loginRequest))
                .expectErrorMatches(throwable ->
                        throwable instanceof InvalidPasswordException &&
                                INVALID_PASSWORD.equals(throwable.getMessage()))
                .verify();
    }

    @Test
    void login_WithDisabledUser_ShouldThrowException() {
        userModel.setEnabled(false);
        when(userServicePort.findByUsername(anyString())).thenReturn(Mono.just(userModel));

        StepVerifier.create(authService.login(loginRequest))
                .expectErrorMatches(throwable ->
                        throwable instanceof UserNotEnabledException &&
                                ACCOUNT_IS_DISABLED.equals(throwable.getMessage()))
                .verify();
    }

    @Test
    void register_ShouldHandleError_WhenRegistrationFails() {
        when(userDtoMapper.toUserDomain(any(RegisterUserRequest.class))).thenReturn(userModel);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(authServicePort.registerUser(any(UserModel.class))).thenReturn(Mono.error(new RuntimeException("Database error")));

        StepVerifier.create(authService.register(registerRequest))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void login_WithUserNotFound_ShouldThrowException() {
        when(userServicePort.findByUsername(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(authService.login(loginRequest))
                .expectErrorMatches(throwable ->
                        throwable instanceof UserNotFoundException &&
                                "User not found".equals(throwable.getMessage()))
                .verify();
    }
}