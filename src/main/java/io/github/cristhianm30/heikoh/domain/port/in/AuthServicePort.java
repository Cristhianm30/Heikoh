package io.github.cristhianm30.heikoh.domain.port.in;

import io.github.cristhianm30.heikoh.domain.model.LoginData;
import io.github.cristhianm30.heikoh.domain.model.UserModel;
import reactor.core.publisher.Mono;

public interface AuthServicePort {
    Mono<UserModel> registerUser(UserModel userToRegister);
    Mono<LoginData> loginUser(UserModel userModel);
}
