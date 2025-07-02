package io.github.cristhianm30.heikoh.infrastructure.configuration.bean;


import io.github.cristhianm30.heikoh.domain.port.in.AuthServicePort;
import io.github.cristhianm30.heikoh.domain.port.in.UserServicePort;
import io.github.cristhianm30.heikoh.domain.port.out.ExpenseRepositoryPort;
import io.github.cristhianm30.heikoh.domain.port.out.IncomeRepositoryPort;
import io.github.cristhianm30.heikoh.domain.port.out.JwtPort;
import io.github.cristhianm30.heikoh.domain.port.out.UserRepositoryPort;
import io.github.cristhianm30.heikoh.domain.usecase.AuthUseCase;
import io.github.cristhianm30.heikoh.domain.usecase.RegisterExpenseUseCase;
import io.github.cristhianm30.heikoh.domain.usecase.RegisterIncomeUseCase;
import io.github.cristhianm30.heikoh.domain.usecase.UserUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainBeanConfig {

    @Bean
    public AuthServicePort AuthUseCase(UserServicePort userServicePort, JwtPort jwtPort) {
        return new AuthUseCase(userServicePort,jwtPort);
    }
    @Bean
    public UserServicePort UserUseCase(UserRepositoryPort userRepositoryPort) {
        return new UserUseCase(userRepositoryPort);
    }

    @Bean
    public RegisterExpenseUseCase registerExpenseUseCase(ExpenseRepositoryPort expenseRepositoryPort) {
        return new RegisterExpenseUseCase(expenseRepositoryPort);
    }

    @Bean
    public RegisterIncomeUseCase registerIncomeUseCase(IncomeRepositoryPort incomeRepositoryPort) {
        return new RegisterIncomeUseCase(incomeRepositoryPort);
    }

}