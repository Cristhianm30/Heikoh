package io.github.cristhianm30.heikoh.domain.usecase;

import io.github.cristhianm30.heikoh.domain.exception.UserAlreadyExistsException;
import io.github.cristhianm30.heikoh.domain.model.LoginData;
import io.github.cristhianm30.heikoh.domain.model.UserModel;
import io.github.cristhianm30.heikoh.domain.port.in.AuthServicePort;
import io.github.cristhianm30.heikoh.domain.port.in.UserServicePort;
import io.github.cristhianm30.heikoh.domain.port.out.JwtPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static io.github.cristhianm30.heikoh.domain.util.constant.ExceptionConstants.EMAIL_ALREADY_IN_USE;
import static io.github.cristhianm30.heikoh.domain.util.constant.ExceptionConstants.USERNAME_ALREADY_IN_USE;


@RequiredArgsConstructor
public class AuthUseCase implements AuthServicePort {


    private final UserServicePort userServicePort;
    private final JwtPort jwtPort;

    @Override
    public Mono<UserModel> registerUser(UserModel userToRegister) {
        return userServicePort.findByUsernameOrEmail(userToRegister.getUsername(), userToRegister.getEmail())
                .flatMap(existingUser -> {
                    if (existingUser.getUsername().equals(userToRegister.getUsername())) {
                        return Mono.<UserModel>error(new UserAlreadyExistsException(USERNAME_ALREADY_IN_USE));
                    } else {
                        return Mono.error(new UserAlreadyExistsException(EMAIL_ALREADY_IN_USE));
                    }
                })
                .switchIfEmpty(Mono.defer(() -> {
                    UserModel userWithMetadata = UserModel.builder()
                            .username(userToRegister.getUsername())
                            .email(userToRegister.getEmail())
                            .password(userToRegister.getPassword())
                            .enabled(true)
                            .role(userToRegister.getRole())
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();
                    return userServicePort.save(userWithMetadata);
                }));
    }

    @Override
    public Mono<LoginData> loginUser(UserModel authenticatedUser) {
        return jwtPort.generateToken(authenticatedUser)
                .map(token -> LoginData.builder()
                        .token(token)
                        .id(authenticatedUser.getId())
                        .username(authenticatedUser.getUsername())
                        .email(authenticatedUser.getEmail())
                        .enabled(authenticatedUser.getEnabled())
                        .role(authenticatedUser.getRole())
                        .build());
    }


}
