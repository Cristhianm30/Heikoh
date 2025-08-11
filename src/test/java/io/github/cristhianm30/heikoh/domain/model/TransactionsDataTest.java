package io.github.cristhianm30.heikoh.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionsDataTest {

    @Test
    void testTransactionData() {
        Integer year = 2024;
        Integer month = 7;
        Integer limit = 10;
        Integer offset = 0;
        String type = "expense";

        TransactionsData transactionsData = new TransactionsData(year, month, limit, offset, type);

        assertEquals(year, transactionsData.getYear());
        assertEquals(month, transactionsData.getMonth());
        assertEquals(limit, transactionsData.getLimit());
        assertEquals(offset, transactionsData.getOffset());
        assertEquals(type, transactionsData.getType()); // Added assertion for type
    }

    @Test
    void testSetters() {
        TransactionsData transactionsData = new TransactionsData(); // Use no-arg constructor

        Integer newYear = 2025;
        Integer newMonth = 8;
        Integer newLimit = 20;
        Integer newOffset = 5;
        String newType = "income";

        transactionsData.setYear(newYear);
        transactionsData.setMonth(newMonth);
        transactionsData.setLimit(newLimit);
        transactionsData.setOffset(newOffset);
        transactionsData.setType(newType);

        assertEquals(newYear, transactionsData.getYear());
        assertEquals(newMonth, transactionsData.getMonth());
        assertEquals(newLimit, transactionsData.getLimit());
        assertEquals(newOffset, transactionsData.getOffset());
        assertEquals(newType, transactionsData.getType());
    }

    @Test
    void testBuilder() {
        Integer year = 2023;
        Integer month = 6;
        Integer limit = 5;
        Integer offset = 1;
        String type = "savings";

        TransactionsData transactionsData = TransactionsData.builder()
                .year(year)
                .month(month)
                .limit(limit)
                .offset(offset)
                .type(type)
                .build();

        assertEquals(year, transactionsData.getYear());
        assertEquals(month, transactionsData.getMonth());
        assertEquals(limit, transactionsData.getLimit());
        assertEquals(offset, transactionsData.getOffset());
        assertEquals(type, transactionsData.getType());
    }
}