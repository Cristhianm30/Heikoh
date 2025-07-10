package io.github.cristhianm30.heikoh.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AggregationDataTest {

    @Test
    void testAggregationDataCreation() {
        String key = "Food";
        BigDecimal totalAmount = new BigDecimal("150.75");

        AggregationData data = new AggregationData(key, totalAmount);

        assertEquals(key, data.getKey());
        assertEquals(totalAmount, data.getTotalAmount());
    }

    @Test
    void testAggregationDataBuilder() {
        String key = "Utilities";
        BigDecimal totalAmount = new BigDecimal("200.00");

        AggregationData data = AggregationData.builder()
                .key(key)
                .totalAmount(totalAmount)
                .build();

        assertEquals(key, data.getKey());
        assertEquals(totalAmount, data.getTotalAmount());
    }
}

