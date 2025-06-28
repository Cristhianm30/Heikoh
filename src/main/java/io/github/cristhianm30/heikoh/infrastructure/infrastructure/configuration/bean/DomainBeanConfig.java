package io.github.cristhianm30.heikoh.infrastructure.infrastructure.configuration.bean;


import io.github.cristhianm30.heikoh.domain.port.in.UserServicePort;
import io.github.cristhianm30.heikoh.domain.port.out.UserRepositoryPort;
import io.github.cristhianm30.heikoh.domain.usecase.RegisterUserUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainBeanConfig {

    @Bean
    public UserServicePort registerUserUseCase(UserRepositoryPort userRepositoryPort) {
        return new RegisterUserUseCase(userRepositoryPort);
    }

}