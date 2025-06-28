package io.github.cristhianm30.Heikoh.domain.port.in;

import io.github.cristhianm30.Heikoh.domain.model.UserModel;
import reactor.core.publisher.Mono;

public interface UserServicePort {
    Mono<UserModel> registerUser(UserModel userToRegister);
}
