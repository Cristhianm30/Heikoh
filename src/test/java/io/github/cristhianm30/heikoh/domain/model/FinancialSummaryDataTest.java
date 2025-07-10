package io.github.cristhianm30.heikoh.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class FinancialSummaryDataTest {

    @Test
    void testFinancialSummaryDataCreation() {
        BigDecimal totalIncome = new BigDecimal("1000.00");
        BigDecimal totalExpense = new BigDecimal("500.00");
        BigDecimal totalBalance = new BigDecimal("500.00");

        FinancialSummaryData summary = new FinancialSummaryData(totalIncome, totalExpense, totalBalance);

        assertEquals(totalIncome, summary.getTotalIncome());
        assertEquals(totalExpense, summary.getTotalExpense());
        assertEquals(totalBalance, summary.getTotalBalance());
    }

    @Test
    void testFinancialSummaryDataBuilder() {
        BigDecimal totalIncome = new BigDecimal("2000.00");
        BigDecimal totalExpense = new BigDecimal("750.00");
        BigDecimal totalBalance = new BigDecimal("1250.00");

        FinancialSummaryData summary = FinancialSummaryData.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .totalBalance(totalBalance)
                .build();

        assertEquals(totalIncome, summary.getTotalIncome());
        assertEquals(totalExpense, summary.getTotalExpense());
        assertEquals(totalBalance, summary.getTotalBalance());
    }

}
