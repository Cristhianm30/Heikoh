package io.github.cristhianm30.heikoh.domain.port.in;

import io.github.cristhianm30.heikoh.domain.model.UserModel;
import reactor.core.publisher.Mono;

public interface UserServicePort {
    Mono<UserModel> findByUsername(String username);
    Mono<UserModel> findByUsernameOrEmail(String username, String email);
    Mono<UserModel> save(UserModel user);
}
