package io.github.cristhianm30.heikoh.domain.usecase;

import io.github.cristhianm30.heikoh.domain.exception.UserAlreadyExistsException;
import io.github.cristhianm30.heikoh.domain.model.LoginData;
import io.github.cristhianm30.heikoh.domain.model.UserModel;
import io.github.cristhianm30.heikoh.domain.port.in.UserServicePort;
import io.github.cristhianm30.heikoh.domain.port.out.JwtPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static io.github.cristhianm30.heikoh.domain.util.constant.ExceptionConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthUseCaseTest {

    @Mock
    private UserServicePort userServicePort;

    @Mock
    private JwtPort jwtPort;

    @InjectMocks
    private AuthUseCase authUseCase;

    private UserModel testUser;
    private LoginData testLoginData;

    @BeforeEach
    void setUp() {
        testUser = new UserModel();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setEnabled(true);
        testUser.setRole("USER");
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        testLoginData = new LoginData();
        testLoginData.setToken("testToken");
        testLoginData.setId(1L);
        testLoginData.setUsername("testuser");
        testLoginData.setEmail("test@example.com");
        testLoginData.setEnabled(true);
        testLoginData.setRole("USER");
    }

    @Test
    void registerUser_WhenNewUser_ShouldRegisterSuccessfully() {
        // Arrange
        when(userServicePort.findByUsernameOrEmail(anyString(), anyString()))
                .thenReturn(Mono.empty());
        when(userServicePort.save(any(UserModel.class)))
                .thenReturn(Mono.just(testUser));

        // Act & Assert
        StepVerifier.create(authUseCase.registerUser(testUser))
                .expectNextMatches(user ->
                        user.getUsername().equals("testuser") &&
                                user.getEmail().equals("test@example.com") &&
                                user.getCreatedAt() != null)
                .verifyComplete();
    }

    @Test
    void registerUser_WhenUsernameExists_ShouldThrowException() {
        // Arrange
        UserModel existingUser = new UserModel();
        existingUser.setId(2L);
        existingUser.setUsername("testuser");
        existingUser.setEmail("different@example.com");
        when(userServicePort.findByUsernameOrEmail(anyString(), anyString()))
                .thenReturn(Mono.just(existingUser));

        // Act & Assert
        StepVerifier.create(authUseCase.registerUser(testUser))
                .expectErrorMatches(throwable ->
                        throwable instanceof UserAlreadyExistsException &&
                                throwable.getMessage().equals(USERNAME_ALREADY_IN_USE))
                .verify();
    }

    @Test
    void registerUser_WhenEmailExists_ShouldThrowException() {
        // Arrange
        UserModel existingUser = new UserModel();
        existingUser.setId(2L);
        existingUser.setUsername("differentuser");
        existingUser.setEmail("test@example.com");
        when(userServicePort.findByUsernameOrEmail(anyString(), anyString()))
                .thenReturn(Mono.just(existingUser));

        // Act & Assert
        StepVerifier.create(authUseCase.registerUser(testUser))
                .expectErrorMatches(throwable ->
                        throwable instanceof UserAlreadyExistsException &&
                                throwable.getMessage().equals(EMAIL_ALREADY_IN_USE))
                .verify();
    }

    @Test
    void loginUser_WhenValidUser_ShouldReturnLoginData() {
        // Arrange
        when(jwtPort.generateToken(any(UserModel.class)))
                .thenReturn(Mono.just("testToken"));

        // Act & Assert
        StepVerifier.create(authUseCase.loginUser(testUser))
                .expectNextMatches(loginData ->
                        loginData.getToken().equals("testToken") &&
                                loginData.getUsername().equals("testuser") &&
                                loginData.getId().equals(1L))
                .verifyComplete();
    }

    @Test
    void loginUser_WhenTokenGenerationFails_ShouldPropagateError() {
        // Arrange
        when(jwtPort.generateToken(any(UserModel.class)))
                .thenReturn(Mono.error(new RuntimeException("Token generation failed")));

        // Act & Assert
        StepVerifier.create(authUseCase.loginUser(testUser))
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().equals("Token generation failed"))
                .verify();
    }
}