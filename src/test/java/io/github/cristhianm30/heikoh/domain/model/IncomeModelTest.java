package io.github.cristhianm30.heikoh.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class IncomeModelTest {

    @Test
    void testNoArgsConstructor() {
        IncomeModel incomeModel = new IncomeModel();
        assertNotNull(incomeModel);
    }

    @Test
    void testAllArgsConstructor() {
        Long id = 1L;
        Long userId = 101L;
        BigDecimal amount = BigDecimal.valueOf(100.00);
        String description = "Salary";
        LocalDate transactionDate = LocalDate.now();
        String origin = "Work";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        IncomeModel incomeModel = new IncomeModel(id, userId, amount, description, transactionDate, origin, createdAt, updatedAt);

        assertEquals(id, incomeModel.getId());
        assertEquals(userId, incomeModel.getUserId());
        assertEquals(amount, incomeModel.getAmount());
        assertEquals(description, incomeModel.getDescription());
        assertEquals(transactionDate, incomeModel.getTransactionDate());
        assertEquals(origin, incomeModel.getOrigin());
        assertEquals(createdAt, incomeModel.getCreatedAt());
        assertEquals(updatedAt, incomeModel.getUpdatedAt());
    }

    @Test
    void testBuilder() {
        Long id = 2L;
        Long userId = 102L;
        BigDecimal amount = BigDecimal.valueOf(200.00);
        String description = "Freelance";
        LocalDate transactionDate = LocalDate.now().minusDays(1);
        String origin = "Client";
        LocalDateTime createdAt = LocalDateTime.now().minusHours(1);
        LocalDateTime updatedAt = LocalDateTime.now().minusMinutes(30);

        IncomeModel incomeModel = IncomeModel.builder()
                .id(id)
                .userId(userId)
                .amount(amount)
                .description(description)
                .transactionDate(transactionDate)
                .origin(origin)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        assertEquals(id, incomeModel.getId());
        assertEquals(userId, incomeModel.getUserId());
        assertEquals(amount, incomeModel.getAmount());
        assertEquals(description, incomeModel.getDescription());
        assertEquals(transactionDate, incomeModel.getTransactionDate());
        assertEquals(origin, incomeModel.getOrigin());
        assertEquals(createdAt, incomeModel.getCreatedAt());
        assertEquals(updatedAt, incomeModel.getUpdatedAt());
    }

    @Test
    void testSetters() {
        IncomeModel incomeModel = new IncomeModel();
        Long id = 3L;
        Long userId = 103L;
        BigDecimal amount = BigDecimal.valueOf(300.00);
        String description = "Bonus";
        LocalDate transactionDate = LocalDate.now().minusDays(2);
        String origin = "Company";
        LocalDateTime createdAt = LocalDateTime.now().minusHours(2);
        LocalDateTime updatedAt = LocalDateTime.now().minusMinutes(45);

        incomeModel.setId(id);
        incomeModel.setUserId(userId);
        incomeModel.setAmount(amount);
        incomeModel.setDescription(description);
        incomeModel.setTransactionDate(transactionDate);
        incomeModel.setOrigin(origin);
        incomeModel.setCreatedAt(createdAt);
        incomeModel.setUpdatedAt(updatedAt);

        assertEquals(id, incomeModel.getId());
        assertEquals(userId, incomeModel.getUserId());
        assertEquals(amount, incomeModel.getAmount());
        assertEquals(description, incomeModel.getDescription());
        assertEquals(transactionDate, incomeModel.getTransactionDate());
        assertEquals(origin, incomeModel.getOrigin());
        assertEquals(createdAt, incomeModel.getCreatedAt());
        assertEquals(updatedAt, incomeModel.getUpdatedAt());
    }
}
