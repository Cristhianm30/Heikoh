package io.github.cristhianm30.heikoh.domain.usecase;

import io.github.cristhianm30.heikoh.domain.exception.UserNotFoundException;
import io.github.cristhianm30.heikoh.domain.model.UserModel;
import io.github.cristhianm30.heikoh.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static io.github.cristhianm30.heikoh.domain.util.constant.AuthConstant.USER_NOT_FOUND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @InjectMocks
    private UserUseCase userUseCase;

    private UserModel testUser;

    @BeforeEach
    void setUp() {
        testUser = UserModel.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .enabled(true)
                .role("USER")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void findByUsername_WhenUserExists_ShouldReturnUser() {

        when(userRepositoryPort.findByUsername(anyString()))
                .thenReturn(Mono.just(testUser));


        StepVerifier.create(userUseCase.findByUsername("testuser"))
                .expectNextMatches(user ->
                        user.getUsername().equals("testuser") &&
                                user.getEmail().equals("test@example.com"))
                .verifyComplete();
    }

    @Test
    void findByUsername_WhenUserNotExists_ShouldThrowException() {

        when(userRepositoryPort.findByUsername(anyString()))
                .thenReturn(Mono.empty());


        StepVerifier.create(userUseCase.findByUsername("nonexistent"))
                .expectErrorMatches(throwable ->
                        throwable instanceof UserNotFoundException &&
                                throwable.getMessage().equals(USER_NOT_FOUND))
                .verify();
    }

    @Test
    void findByUsernameOrEmail_WhenUserExistsByUsername_ShouldReturnUser() {
        // Arrange
        when(userRepositoryPort.findByUsernameOrEmail(anyString(), anyString()))
                .thenReturn(Mono.just(testUser));


        StepVerifier.create(userUseCase.findByUsernameOrEmail("testuser", "other@example.com"))
                .expectNext(testUser)
                .verifyComplete();
    }

    @Test
    void findByUsernameOrEmail_WhenUserExistsByEmail_ShouldReturnUser() {

        when(userRepositoryPort.findByUsernameOrEmail(anyString(), anyString()))
                .thenReturn(Mono.just(testUser));


        StepVerifier.create(userUseCase.findByUsernameOrEmail("otheruser", "test@example.com"))
                .expectNext(testUser)
                .verifyComplete();
    }

    @Test
    void save_WhenValidUser_ShouldReturnSavedUser() {
        when(userRepositoryPort.save(any(UserModel.class)))
                .thenReturn(Mono.just(testUser));

        StepVerifier.create(userUseCase.save(testUser))
                .expectNextMatches(user ->
                        user.getId().equals(1L) &&
                                user.getUsername().equals("testuser"))
                .verifyComplete();
    }

    @Test
    void findByUsernameOrEmail_WhenUserNotExists_ShouldReturnEmptyMono() {
        // Arrange
        when(userRepositoryPort.findByUsernameOrEmail(anyString(), anyString()))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(userUseCase.findByUsernameOrEmail("nonexistent", "nonexistent@example.com"))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void save_WhenPersistenceFails_ShouldReturnError() {
        // Arrange
        when(userRepositoryPort.save(any(UserModel.class)))
                .thenReturn(Mono.error(new RuntimeException("DB Error")));

        // Act & Assert
        StepVerifier.create(userUseCase.save(testUser))
                .expectError(RuntimeException.class)
                .verify();
    }
}