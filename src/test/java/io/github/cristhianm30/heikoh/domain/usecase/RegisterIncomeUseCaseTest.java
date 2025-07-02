package io.github.cristhianm30.heikoh.domain.usecase;

import io.github.cristhianm30.heikoh.domain.model.IncomeModel;
import io.github.cristhianm30.heikoh.domain.port.out.IncomeRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterIncomeUseCaseTest {

    @Mock
    private IncomeRepositoryPort incomeRepositoryPort;

    @InjectMocks
    private RegisterIncomeUseCase registerIncomeUseCase;

    private IncomeModel incomeModel;

    @BeforeEach
    void setUp() {
        incomeModel = IncomeModel.builder()
                .amount(BigDecimal.valueOf(100.00))
                .description("Salary")
                .transactionDate(LocalDate.now())
                .origin("Work")
                .userId(1L)
                .build();
    }

    @Test
    void registerIncome_ShouldReturnIncomeModel_WhenSuccessful() {
        when(incomeRepositoryPort.save(any(IncomeModel.class))).thenReturn(Mono.just(incomeModel));

        StepVerifier.create(registerIncomeUseCase.registerIncome(incomeModel))
                .expectNext(incomeModel)
                .verifyComplete();
    }

    @Test
    void registerIncome_ShouldReturnError_WhenPersistenceFails() {
        when(incomeRepositoryPort.save(any(IncomeModel.class))).thenReturn(Mono.error(new RuntimeException("DB Error")));

        StepVerifier.create(registerIncomeUseCase.registerIncome(incomeModel))
                .expectError(RuntimeException.class)
                .verify();
    }
}
