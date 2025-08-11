package io.github.cristhianm30.heikoh.application.service;

import io.github.cristhianm30.heikoh.application.dto.request.LoginRequest;
import io.github.cristhianm30.heikoh.application.dto.request.RegisterUserRequest;
import io.github.cristhianm30.heikoh.application.dto.response.LoginResponse;
import io.github.cristhianm30.heikoh.application.dto.response.UserResponse;
import reactor.core.publisher.Mono;

public interface AuthService {

    Mono<UserResponse> register(RegisterUserRequest request);

    Mono<LoginResponse> login(LoginRequest request);

    Mono<LoginResponse> refresh(String username);

}
