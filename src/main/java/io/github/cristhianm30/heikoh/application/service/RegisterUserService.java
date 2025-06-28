package io.github.cristhianm30.Heikoh.application.service;

import io.github.cristhianm30.Heikoh.application.dto.request.RegisterUserRequest;
import io.github.cristhianm30.Heikoh.application.dto.response.UserResponse;
import reactor.core.publisher.Mono;

public interface RegisterUserService {

    Mono<UserResponse> register(RegisterUserRequest request);

}
