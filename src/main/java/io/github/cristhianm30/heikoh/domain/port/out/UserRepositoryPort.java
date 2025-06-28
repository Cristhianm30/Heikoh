package io.github.cristhianm30.Heikoh.domain.port.out;

import io.github.cristhianm30.Heikoh.domain.model.UserModel;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRepositoryPort {
    Mono<UserModel> save(UserModel user);
    Mono<UserModel> findByUsername(String username);
    Mono<UserModel> findById(UUID id);
    Mono<Boolean> existsByUsername(String username);
    Mono<Boolean> existsByEmail(String email);
}