package io.github.cristhianm30.heikoh.application.service.impl;

import io.github.cristhianm30.heikoh.application.dto.request.RegisterUserRequest;
import io.github.cristhianm30.heikoh.application.dto.response.UserResponse;
import io.github.cristhianm30.heikoh.application.mapper.UserDtoMapper;
import io.github.cristhianm30.heikoh.application.service.RegisterUserService;
import io.github.cristhianm30.heikoh.domain.model.UserModel;
import io.github.cristhianm30.heikoh.domain.port.in.UserServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static io.github.cristhianm30.heikoh.domain.util.constant.ApplicationConstant.DEFAULT_USER_ROLE;

@Service
@RequiredArgsConstructor
public class RegisterUserServiceImpl implements RegisterUserService {

    private final UserServicePort userServicePort;
    private final UserDtoMapper userDtoMapper;
    private final PasswordEncoder passwordEncoder;

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

        return userServicePort.registerUser(userWithHashedPasswordAndRole)
                .map(userDtoMapper::toUserResponse);
    }

    private UserModel toUserDomain(RegisterUserRequest request) {
        if (request == null) {
            return null;
        }

        return UserModel.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

}
