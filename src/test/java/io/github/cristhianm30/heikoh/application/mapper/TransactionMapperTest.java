package io.github.cristhianm30.heikoh.application.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class TransactionMapperTest {

    @Spy
    @InjectMocks
    private TransactionMapper transactionMapper = new TransactionMapperImpl(); // Use the generated implementation

    @BeforeEach
    void setUp() {
        // No specific setup needed for this test class as we are testing a default method
    }

    @Test
    void roundAmount_ShouldRoundPositiveValueCorrectly() {
        BigDecimal original = new BigDecimal("123.456");
        BigDecimal expected = new BigDecimal("123.46");
        assertEquals(expected, transactionMapper.roundAmount(original));
    }

    @Test
    void roundAmount_ShouldRoundNegativeValueCorrectly() {
        BigDecimal original = new BigDecimal("-123.456");
        BigDecimal expected = new BigDecimal("-123.46");
        assertEquals(expected, transactionMapper.roundAmount(original));
    }

    @Test
    void roundAmount_ShouldHandleValueWithExactlyTwoDecimalPlaces() {
        BigDecimal original = new BigDecimal("123.45");
        BigDecimal expected = new BigDecimal("123.45");
        assertEquals(expected, transactionMapper.roundAmount(original));
    }

    @Test
    void roundAmount_ShouldHandleValueWithFewerThanTwoDecimalPlaces() {
        BigDecimal original = new BigDecimal("123.4");
        BigDecimal expected = new BigDecimal("123.40");
        assertEquals(expected, transactionMapper.roundAmount(original));
    }

    @Test
    void roundAmount_ShouldReturnNullForNullInput() {
        assertNull(transactionMapper.roundAmount(null));
    }

    @Test
    void roundAmount_ShouldRoundUpWhenThirdDecimalIsFiveOrGreater() {
        BigDecimal original = new BigDecimal("10.125");
        BigDecimal expected = new BigDecimal("10.13");
        assertEquals(expected, transactionMapper.roundAmount(original));
    }

    @Test
    void roundAmount_ShouldNotRoundUpWhenThirdDecimalIsLessThanFive() {
        BigDecimal original = new BigDecimal("10.124");
        BigDecimal expected = new BigDecimal("10.12");
        assertEquals(expected, transactionMapper.roundAmount(original));
    }
}
