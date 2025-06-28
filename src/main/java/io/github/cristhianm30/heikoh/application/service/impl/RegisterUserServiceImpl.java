package io.github.cristhianm30.Heikoh.application.service.impl;

import io.github.cristhianm30.Heikoh.application.dto.request.RegisterUserRequest;
import io.github.cristhianm30.Heikoh.application.dto.response.UserResponse;
import io.github.cristhianm30.Heikoh.application.mapper.UserDtoMapper;
import io.github.cristhianm30.Heikoh.application.service.RegisterUserService;
import io.github.cristhianm30.Heikoh.domain.model.UserModel;
import io.github.cristhianm30.Heikoh.domain.port.in.UserServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static io.github.cristhianm30.Heikoh.domain.util.constant.ApplicationConstant.DEFAULT_USER_ROLE;

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

}
