package io.github.cristhianm30.heikoh.application.service;

import io.github.cristhianm30.heikoh.application.dto.request.RegisterUserRequest;
import io.github.cristhianm30.heikoh.application.dto.response.UserResponse;
import reactor.core.publisher.Mono;

public interface RegisterUserService {

    Mono<UserResponse> register(RegisterUserRequest request);

}
