package io.github.cristhianm30.heikoh.infrastructure.configuration.bean;


import io.github.cristhianm30.heikoh.domain.port.in.*;
import io.github.cristhianm30.heikoh.domain.port.out.ExpenseRepositoryPort;
import io.github.cristhianm30.heikoh.domain.port.out.IncomeRepositoryPort;
import io.github.cristhianm30.heikoh.domain.port.out.JwtPort;
import io.github.cristhianm30.heikoh.domain.port.out.UserRepositoryPort;
import io.github.cristhianm30.heikoh.domain.usecase.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainBeanConfig {

    @Bean
    public AuthServicePort AuthUseCase(UserServicePort userServicePort, JwtPort jwtPort) {
        return new AuthUseCase(userServicePort, jwtPort);
    }

    @Bean
    public UserServicePort UserUseCase(UserRepositoryPort userRepositoryPort) {
        return new UserUseCase(userRepositoryPort);
    }

    @Bean
    public TransactionUseCase getTransactionsUseCase(ExpenseRepositoryPort expenseRepositoryPort, IncomeRepositoryPort incomeRepositoryPort) {
        return new TransactionUseCase(expenseRepositoryPort, incomeRepositoryPort);
    }


    @Bean
    public UpdateTransactionServicePort updateTransactionServicePort(
            ExpenseRepositoryPort expenseRepositoryPort,
            IncomeRepositoryPort incomeRepositoryPort) {
        return new UpdateTransactionUseCase(expenseRepositoryPort, incomeRepositoryPort);
    }

    @Bean
    public DeleteTransactionServicePort deleteTransactionServicePort(
            ExpenseRepositoryPort expenseRepositoryPort,
            IncomeRepositoryPort incomeRepositoryPort) {
        return new DeleteTransactionUseCase(expenseRepositoryPort, incomeRepositoryPort);
    }

    @Bean
    public RegisterTransactionServicePort registerTransactionServicePort(
            ExpenseRepositoryPort expenseRepositoryPort,
            IncomeRepositoryPort incomeRepositoryPort) {
        return new RegisterTransactionUseCase(expenseRepositoryPort, incomeRepositoryPort);
    }


}