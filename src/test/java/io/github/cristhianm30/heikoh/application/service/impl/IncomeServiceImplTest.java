package io.github.cristhianm30.heikoh.application.service.impl;

import io.github.cristhianm30.heikoh.application.dto.request.IncomeRequest;
import io.github.cristhianm30.heikoh.application.dto.response.IncomeResponse;
import io.github.cristhianm30.heikoh.application.mapper.IncomeMapper;
import io.github.cristhianm30.heikoh.domain.exception.IncomeNotRegisteredException;
import io.github.cristhianm30.heikoh.domain.model.IncomeModel;
import io.github.cristhianm30.heikoh.domain.port.in.RegisterIncomeServicePort;
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

import static io.github.cristhianm30.heikoh.domain.util.constant.ExceptionConstants.INCOME_NOT_REGISTERED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IncomeServiceImplTest {

    @Mock
    private RegisterIncomeServicePort registerIncomeServicePort;

    @Mock
    private IncomeMapper incomeMapper;

    @InjectMocks
    private IncomeServiceImpl incomeService;

    private IncomeRequest incomeRequest;
    private IncomeModel incomeModel;
    private IncomeResponse incomeResponse;

    @BeforeEach
    void setUp() {
        incomeRequest = new IncomeRequest(
                BigDecimal.valueOf(100.00),
                "Salary",
                LocalDate.now(),
                "Work"
        );

        incomeModel = IncomeModel.builder()
                .amount(BigDecimal.valueOf(100.00))
                .description("Salary")
                .transactionDate(LocalDate.now())
                .origin("Work")
                .userId(1L)
                .build();

        incomeResponse = new IncomeResponse(
                1L,
                1L,
                BigDecimal.valueOf(100.00),
                "Salary",
                LocalDate.now(),
                "Work",
                null,
                null
        );
    }

    @Test
    void registerIncome_ShouldReturnIncomeResponse_WhenSuccessful() {
        when(incomeMapper.toModel(any(IncomeRequest.class))).thenReturn(incomeModel);
        when(registerIncomeServicePort.registerIncome(any(IncomeModel.class))).thenReturn(Mono.just(incomeModel));
        when(incomeMapper.toResponse(any(IncomeModel.class))).thenReturn(incomeResponse);

        StepVerifier.create(incomeService.registerIncome(incomeRequest, 1L))
                .expectNextMatches(response -> response.getId().equals(1L) && response.getAmount().equals(BigDecimal.valueOf(100.00)))
                .verifyComplete();
    }

    @Test
    void registerIncome_ShouldThrowIncomeNotRegisteredException_WhenRegistrationFails() {
        when(incomeMapper.toModel(any(IncomeRequest.class))).thenReturn(incomeModel);
        when(registerIncomeServicePort.registerIncome(any(IncomeModel.class))).thenReturn(Mono.error(new RuntimeException("DB Error")));

        StepVerifier.create(incomeService.registerIncome(incomeRequest, 1L))
                .expectErrorMatches(throwable ->
                        throwable instanceof IncomeNotRegisteredException &&
                                INCOME_NOT_REGISTERED.equals(throwable.getMessage()))
                .verify();
    }
}
