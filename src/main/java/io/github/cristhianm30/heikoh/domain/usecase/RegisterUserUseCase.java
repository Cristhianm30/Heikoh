package io.github.cristhianm30.heikoh.domain.usecase;

import io.github.cristhianm30.heikoh.domain.exception.UserAlreadyExistsException;
import io.github.cristhianm30.heikoh.domain.model.UserModel;
import io.github.cristhianm30.heikoh.domain.port.in.UserServicePort;
import io.github.cristhianm30.heikoh.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

import static io.github.cristhianm30.heikoh.domain.util.constant.ExceptionConstants.EMAIL_ALREADY_IN_USE;
import static io.github.cristhianm30.heikoh.domain.util.constant.ExceptionConstants.USERNAME_ALREADY_IN_USE;


@RequiredArgsConstructor
public class RegisterUserUseCase implements UserServicePort {


    private final UserRepositoryPort userRepositoryPort;

    @Override
    public Mono<UserModel> registerUser(UserModel userToRegister) {
        return userRepositoryPort.existsByUsername(userToRegister.getUsername())
                .flatMap(usernameExists -> {
                    if (usernameExists) {
                        return Mono.error(new UserAlreadyExistsException(USERNAME_ALREADY_IN_USE));
                    }
                    return userRepositoryPort.existsByEmail(userToRegister.getEmail());
                })
                .flatMap(emailExists -> {
                    if (emailExists) {
                        return Mono.error(new UserAlreadyExistsException(EMAIL_ALREADY_IN_USE));
                    }

                    UserModel userWithMetadata = UserModel.builder()
                            .id(UUID.randomUUID())
                            .username(userToRegister.getUsername())
                            .email(userToRegister.getEmail())
                            .password(userToRegister.getPassword())
                            .enabled(true)
                            .role(userToRegister.getRole())
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();

                    return userRepositoryPort.save(userWithMetadata);
                });
    }
}
