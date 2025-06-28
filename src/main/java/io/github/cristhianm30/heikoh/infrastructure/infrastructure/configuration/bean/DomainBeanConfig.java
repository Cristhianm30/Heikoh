package io.github.cristhianm30.Heikoh.infrastructure.infrastructure.configuration.bean;


import io.github.cristhianm30.Heikoh.domain.port.in.UserServicePort;
import io.github.cristhianm30.Heikoh.domain.port.out.UserRepositoryPort;
import io.github.cristhianm30.Heikoh.domain.usecase.RegisterUserUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainBeanConfig {

    @Bean
    public UserServicePort registerUserUseCase(UserRepositoryPort userRepositoryPort) {
        return new RegisterUserUseCase(userRepositoryPort);
    }

}