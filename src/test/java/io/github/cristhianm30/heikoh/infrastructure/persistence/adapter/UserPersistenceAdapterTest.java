package io.github.cristhianm30.heikoh.infrastructure.persistence.adapter;

import io.github.cristhianm30.heikoh.domain.model.UserModel;
import io.github.cristhianm30.heikoh.infrastructure.entity.UserEntity;
import io.github.cristhianm30.heikoh.infrastructure.mapper.IUserEntityMapper;
import io.github.cristhianm30.heikoh.infrastructure.persistence.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserPersistenceAdapterTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private IUserEntityMapper userEntityMapper;

    @InjectMocks
    private UserPersistenceAdapter userPersistenceAdapter;

    private UserModel testUserModel;
    private UserEntity testUserEntity;

    @BeforeEach
    void setUp() {
        testUserModel = UserModel.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .enabled(true)
                .role("USER")
                .build();

        testUserEntity = new UserEntity();
        testUserEntity.setId(1L);
        testUserEntity.setUsername("testuser");
        testUserEntity.setEmail("test@example.com");
        testUserEntity.setPassword("password");
        testUserEntity.setEnabled(true);
        testUserEntity.setRole("USER");
    }

    @Test
    void save_ShouldReturnSavedUser() {
        when(userEntityMapper.toEntity(any(UserModel.class))).thenReturn(testUserEntity);
        when(userRepository.save(any(UserEntity.class))).thenReturn(Mono.just(testUserEntity));
        when(userEntityMapper.toModel(any(UserEntity.class))).thenReturn(testUserModel);

        StepVerifier.create(userPersistenceAdapter.save(testUserModel))
                .expectNextMatches(user ->
                        user.getId().equals(1L) &&
                                user.getUsername().equals("testuser"))
                .verifyComplete();
    }

    @Test
    void findByUsername_WhenUserExists_ShouldReturnUser() {
        when(userRepository.findByUsername(anyString())).thenReturn(Mono.just(testUserEntity));
        when(userEntityMapper.toModel(any(UserEntity.class))).thenReturn(testUserModel);

        StepVerifier.create(userPersistenceAdapter.findByUsername("testuser"))
                .expectNext(testUserModel)
                .verifyComplete();
    }

    @Test
    void findByUsername_WhenUserNotExists_ShouldReturnEmpty() {
        when(userRepository.findByUsername(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(userPersistenceAdapter.findByUsername("nonexistent"))
                .verifyComplete();
    }

    @Test
    void findById_WhenUserExists_ShouldReturnUser() {
        when(userRepository.findById(any(Long.class))).thenReturn(Mono.just(testUserEntity));
        when(userEntityMapper.toModel(any(UserEntity.class))).thenReturn(testUserModel);

        StepVerifier.create(userPersistenceAdapter.findById(1L))
                .expectNext(testUserModel)
                .verifyComplete();
    }

    @Test
    void existsByUsername_WhenUsernameExists_ShouldReturnTrue() {
        when(userRepository.existsByUsername(anyString())).thenReturn(Mono.just(true));

        StepVerifier.create(userPersistenceAdapter.existsByUsername("testuser"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void existsByEmail_WhenEmailExists_ShouldReturnTrue() {
        when(userRepository.existsByEmail(anyString())).thenReturn(Mono.just(true));

        StepVerifier.create(userPersistenceAdapter.existsByEmail("test@example.com"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void findByUsernameOrEmail_WhenUserExists_ShouldReturnUser() {
        when(userRepository.findByUsernameOrEmail(anyString(), anyString()))
                .thenReturn(Mono.just(testUserEntity));
        when(userEntityMapper.toModel(any(UserEntity.class))).thenReturn(testUserModel);

        StepVerifier.create(userPersistenceAdapter.findByUsernameOrEmail("testuser", "test@example.com"))
                .expectNext(testUserModel)
                .verifyComplete();
    }
}