package io.github.cristhianm30.heikoh.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ExpenseModelTest {

    @Test
    void testNoArgsConstructor() {
        ExpenseModel expenseModel = new ExpenseModel();
        assertNotNull(expenseModel);
    }

    @Test
    void testAllArgsConstructor() {
        Long id = 1L;
        Long userId = 101L;
        BigDecimal amount = BigDecimal.valueOf(50.00);
        String description = "Groceries";
        LocalDate transactionDate = LocalDate.now();
        String category = "Food";
        String paymentMethod = "Credit Card";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        ExpenseModel expenseModel = new ExpenseModel(id, userId, amount, description, transactionDate, category, paymentMethod, createdAt, updatedAt);

        assertEquals(id, expenseModel.getId());
        assertEquals(userId, expenseModel.getUserId());
        assertEquals(amount, expenseModel.getAmount());
        assertEquals(description, expenseModel.getDescription());
        assertEquals(transactionDate, expenseModel.getTransactionDate());
        assertEquals(category, expenseModel.getCategory());
        assertEquals(paymentMethod, expenseModel.getPaymentMethod());
        assertEquals(createdAt, expenseModel.getCreatedAt());
        assertEquals(updatedAt, expenseModel.getUpdatedAt());
    }

    @Test
    void testBuilder() {
        Long id = 2L;
        Long userId = 102L;
        BigDecimal amount = BigDecimal.valueOf(75.00);
        String description = "Dinner";
        LocalDate transactionDate = LocalDate.now().minusDays(1);
        String category = "Restaurant";
        String paymentMethod = "Cash";
        LocalDateTime createdAt = LocalDateTime.now().minusHours(1);
        LocalDateTime updatedAt = LocalDateTime.now().minusMinutes(30);

        ExpenseModel expenseModel = ExpenseModel.builder()
                .id(id)
                .userId(userId)
                .amount(amount)
                .description(description)
                .transactionDate(transactionDate)
                .category(category)
                .paymentMethod(paymentMethod)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        assertEquals(id, expenseModel.getId());
        assertEquals(userId, expenseModel.getUserId());
        assertEquals(amount, expenseModel.getAmount());
        assertEquals(description, expenseModel.getDescription());
        assertEquals(transactionDate, expenseModel.getTransactionDate());
        assertEquals(category, expenseModel.getCategory());
        assertEquals(paymentMethod, expenseModel.getPaymentMethod());
        assertEquals(createdAt, expenseModel.getCreatedAt());
        assertEquals(updatedAt, expenseModel.getUpdatedAt());
    }

    @Test
    void testSetters() {
        ExpenseModel expenseModel = new ExpenseModel();
        Long id = 3L;
        Long userId = 103L;
        BigDecimal amount = BigDecimal.valueOf(100.00);
        String description = "Shopping";
        LocalDate transactionDate = LocalDate.now().minusDays(2);
        String category = "Clothes";
        String paymentMethod = "Debit Card";
        LocalDateTime createdAt = LocalDateTime.now().minusHours(2);
        LocalDateTime updatedAt = LocalDateTime.now().minusMinutes(45);

        expenseModel.setId(id);
        expenseModel.setUserId(userId);
        expenseModel.setAmount(amount);
        expenseModel.setDescription(description);
        expenseModel.setTransactionDate(transactionDate);
        expenseModel.setCategory(category);
        expenseModel.setPaymentMethod(paymentMethod);
        expenseModel.setCreatedAt(createdAt);
        expenseModel.setUpdatedAt(updatedAt);

        assertEquals(id, expenseModel.getId());
        assertEquals(userId, expenseModel.getUserId());
        assertEquals(amount, expenseModel.getAmount());
        assertEquals(description, expenseModel.getDescription());
        assertEquals(transactionDate, expenseModel.getTransactionDate());
        assertEquals(category, expenseModel.getCategory());
        assertEquals(paymentMethod, expenseModel.getPaymentMethod());
        assertEquals(createdAt, expenseModel.getCreatedAt());
        assertEquals(updatedAt, expenseModel.getUpdatedAt());
    }
}
