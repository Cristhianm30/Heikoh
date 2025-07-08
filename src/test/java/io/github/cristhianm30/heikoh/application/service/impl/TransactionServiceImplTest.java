package io.github.cristhianm30.heikoh.application.service.impl;

import io.github.cristhianm30.heikoh.application.dto.request.TransactionRequest;
import io.github.cristhianm30.heikoh.application.dto.response.TransactionResponse;
import io.github.cristhianm30.heikoh.application.mapper.TransactionMapper;
import io.github.cristhianm30.heikoh.domain.model.ExpenseModel;
import io.github.cristhianm30.heikoh.domain.model.IncomeModel;
import io.github.cristhianm30.heikoh.domain.model.TransactionData;
import io.github.cristhianm30.heikoh.domain.port.in.GetTransactionsServicePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private GetTransactionsServicePort getTransactionsServicePort;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Long userId;
    private ExpenseModel expenseModel;
    private IncomeModel incomeModel;
    private TransactionResponse expenseTransactionResponse;
    private TransactionResponse incomeTransactionResponse;
    private TransactionRequest transactionRequest;
    private TransactionData transactionData;

    @BeforeEach
    void setUp() {
        userId = 1L;

        expenseModel = ExpenseModel.builder()
                .id(1L)
                .userId(userId)
                .amount(BigDecimal.valueOf(50.00))
                .description("Groceries")
                .transactionDate(LocalDate.now().minusDays(1))
                .build();

        incomeModel = IncomeModel.builder()
                .id(2L)
                .userId(userId)
                .amount(BigDecimal.valueOf(100.00))
                .description("Salary")
                .transactionDate(LocalDate.now())
                .build();

        expenseTransactionResponse = TransactionResponse.builder()
                .type("Gasto")
                .amount(BigDecimal.valueOf(50.00))
                .description("Groceries")
                .transactionDate(LocalDate.now().minusDays(1))
                .build();

        incomeTransactionResponse = TransactionResponse.builder()
                .type("Ingreso")
                .amount(BigDecimal.valueOf(100.00))
                .description("Salary")
                .transactionDate(LocalDate.now())
                .build();

        transactionRequest = TransactionRequest.builder()
                .year(LocalDate.now().getYear())
                .month(LocalDate.now().getMonthValue())
                .limit(10)
                .offset(0)
                .build();
        
        transactionData = new TransactionData(
                transactionRequest.getYear(),
                transactionRequest.getMonth(),
                transactionRequest.getLimit(),
                transactionRequest.getOffset(),
                transactionRequest.getType()
        );
    }

    @Test
    void getTransactions_ShouldReturnMappedTransactions() {
        // Arrange
        when(transactionMapper.toTransactionData(transactionRequest)).thenReturn(transactionData);
        when(getTransactionsServicePort.getTransactions(userId, transactionData))
                .thenReturn(Flux.just(incomeModel, expenseModel));
        when(transactionMapper.toTransactionResponse(incomeModel)).thenReturn(incomeTransactionResponse);
        when(transactionMapper.toTransactionResponse(expenseModel)).thenReturn(expenseTransactionResponse);

        // Act & Assert
        StepVerifier.create(transactionService.getTransactions(userId, transactionRequest))
                .expectNext(incomeTransactionResponse)
                .expectNext(expenseTransactionResponse)
                .verifyComplete();
    }
}