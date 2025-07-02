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
import io.github.cristhianm30.heikoh.domain.exception.UserNotFoundException;
import io.github.cristhianm30.heikoh.domain.model.UserModel;
import io.github.cristhianm30.heikoh.domain.port.in.AuthServicePort;
import io.github.cristhianm30.heikoh.domain.port.in.UserServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static io.github.cristhianm30.heikoh.domain.util.constant.ApplicationConstant.DEFAULT_USER_ROLE;
import static io.github.cristhianm30.heikoh.domain.util.constant.AuthConstant.INVALID_PASSWORD;
import static io.github.cristhianm30.heikoh.domain.util.constant.ExceptionConstants.ACCOUNT_IS_DISABLED;
import static io.github.cristhianm30.heikoh.domain.util.constant.ExceptionConstants.USER_NOT_FOUND;
import static io.github.cristhianm30.heikoh.domain.util.constant.LogConstant.*;

@Slf4j
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
                .doOnSuccess(user -> log.info(USER_REGISTERED_SUCCESSFULLY, user.getUsername()))
                .doOnError(ex -> log.error(USER_REGISTRATION_FAILED, ex.getMessage()))
                .map(userDtoMapper::toUserResponse);
    }

    @Override
    public Mono<LoginResponse> login(LoginRequest request) {
        return userServicePort.findByUsername(request.getUsername())
                .switchIfEmpty(Mono.error(new UserNotFoundException(USER_NOT_FOUND)))
                .flatMap(user -> {
                    if (!user.getEnabled()) {
                        log.warn(USER_NOT_ENABLED, user.getUsername());
                        return Mono.error(new UserNotEnabledException(ACCOUNT_IS_DISABLED));
                    }
                    return Mono.just(user);
                })
                .flatMap(user -> {
                    if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                        log.info(USER_AUTHENTICATED_SUCCESSFULLY, user.getUsername());
                        return authServicePort.loginUser(user)
                                .map(authDtoMapper::toLoginResponse);
                    } else {
                        log.warn(INVALID_PASSWORD_LOG, user.getUsername());
                        return Mono.error(new InvalidPasswordException(INVALID_PASSWORD));
                    }
                });
    }


}
