package io.github.cristhianm30.heikoh.application.service.impl;

import io.github.cristhianm30.heikoh.application.dto.request.LoginRequest;
import io.github.cristhianm30.heikoh.application.dto.request.RegisterUserRequest;
import io.github.cristhianm30.heikoh.application.dto.response.LoginResponse;
import io.github.cristhianm30.heikoh.application.dto.response.UserResponse;
import io.github.cristhianm30.heikoh.application.mapper.AuthDtoMapper;
import io.github.cristhianm30.heikoh.application.mapper.UserDtoMapper;
import io.github.cristhianm30.heikoh.application.service.AuthService;
import io.github.cristhianm30.heikoh.domain.exception.InvalidPasswordException;
import io.github.cristhianm30.heikoh.domain.exception.UserNotEnabledException;
import io.github.cristhianm30.heikoh.domain.model.UserModel;
import io.github.cristhianm30.heikoh.domain.port.in.AuthServicePort;
import io.github.cristhianm30.heikoh.domain.port.in.UserServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static io.github.cristhianm30.heikoh.domain.util.constant.ApplicationConstant.DEFAULT_USER_ROLE;
import static io.github.cristhianm30.heikoh.domain.util.constant.AuthConstant.INVALID_PASSWORD;
import static io.github.cristhianm30.heikoh.domain.util.constant.ExceptionConstants.USER_NOT_ENABLED;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthServicePort authServicePort;
    private final UserDtoMapper userDtoMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserServicePort userServicePort;
    private final AuthDtoMapper authDtoMapper;

    @Override
    public Mono<UserResponse> register(RegisterUserRequest request) {

        UserModel userToRegister = userDtoMapper.toUserDomain(request);

        String encodedPassword = passwordEncoder.encode(userToRegister.getPassword());

        UserModel userWithHashedPasswordAndRole = UserModel.builder()
                .username(userToRegister.getUsername())
                .email(userToRegister.getEmail())
                .password(encodedPassword)
                .role(DEFAULT_USER_ROLE)
                .build();

        return authServicePort.registerUser(userWithHashedPasswordAndRole)
                .map(userDtoMapper::toUserResponse);
    }

    @Override
    public Mono<LoginResponse> login(LoginRequest request) {
        return userServicePort.findByUsername(request.getUsername())
                .flatMap(user -> {
                    if (!user.getEnabled()) {
                        return Mono.error(new UserNotEnabledException(USER_NOT_ENABLED));
                    }
                    return Mono.just(user);
                })
                .flatMap(user -> {
                    if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {

                        return authServicePort.loginUser(user)
                                .map(authDtoMapper::toLoginResponse);
                    } else {
                        return Mono.error(new InvalidPasswordException(INVALID_PASSWORD));
                    }
                });
    }


}
