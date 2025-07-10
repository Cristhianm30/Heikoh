package io.github.cristhianm30.heikoh.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ExpenseAggregationDataTest {

    @Test
    void testExpenseAggregationDataCreation() {
        String key = "Food";
        BigDecimal totalAmount = new BigDecimal("150.75");

        ExpenseAggregationData data = new ExpenseAggregationData(key, totalAmount);

        assertEquals(key, data.getKey());
        assertEquals(totalAmount, data.getTotalAmount());
    }

    @Test
    void testExpenseAggregationDataBuilder() {
        String key = "Utilities";
        BigDecimal totalAmount = new BigDecimal("200.00");

        ExpenseAggregationData data = ExpenseAggregationData.builder()
                .key(key)
                .totalAmount(totalAmount)
                .build();

        assertEquals(key, data.getKey());
        assertEquals(totalAmount, data.getTotalAmount());
    }

}
