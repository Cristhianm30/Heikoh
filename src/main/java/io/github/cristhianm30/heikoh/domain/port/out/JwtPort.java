package io.github.cristhianm30.heikoh.domain.port.out;

import io.github.cristhianm30.heikoh.domain.model.UserModel;
import reactor.core.publisher.Mono;

public interface JwtPort {
    Mono<String> generateToken(UserModel user);

}
