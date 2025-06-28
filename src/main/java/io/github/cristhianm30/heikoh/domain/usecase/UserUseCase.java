package io.github.cristhianm30.heikoh.domain.usecase;

import io.github.cristhianm30.heikoh.domain.exception.UserNotFoundException;
import io.github.cristhianm30.heikoh.domain.model.UserModel;
import io.github.cristhianm30.heikoh.domain.port.in.UserServicePort;
import io.github.cristhianm30.heikoh.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import static io.github.cristhianm30.heikoh.domain.util.constant.AuthConstant.USER_NOT_FOUND;

@RequiredArgsConstructor
public class UserUseCase implements UserServicePort {

    private final UserRepositoryPort userRepositoryPort;

    @Override
    public Mono<UserModel> findByUsername(String username) {
        return userRepositoryPort.findByUsername(username)
                .switchIfEmpty(Mono.error(new UserNotFoundException(USER_NOT_FOUND)));
    }

    @Override
    public Mono<UserModel> findByUsernameOrEmail(String username, String email) {
        return userRepositoryPort.findByUsernameOrEmail(username,email);
    }

    @Override
    public Mono<UserModel> save(UserModel user) {
        return userRepositoryPort.save(user);
    }


}
