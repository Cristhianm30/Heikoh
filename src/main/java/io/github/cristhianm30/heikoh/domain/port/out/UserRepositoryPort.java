package io.github.cristhianm30.heikoh.domain.port.out;

import io.github.cristhianm30.heikoh.domain.model.UserModel;
import reactor.core.publisher.Mono;

public interface UserRepositoryPort {
    Mono<UserModel> save(UserModel user);

    Mono<UserModel> findByUsername(String username);

    Mono<UserModel> findById(Long id);

    Mono<Boolean> existsByUsername(String username);

    Mono<Boolean> existsByEmail(String email);

    Mono<UserModel> findByUsernameOrEmail(String username, String email);

}