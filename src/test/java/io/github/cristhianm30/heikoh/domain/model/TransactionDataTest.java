package io.github.cristhianm30.heikoh.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionDataTest {

    @Test
    void testTransactionData() {
        Integer year = 2024;
        Integer month = 7;
        Integer limit = 10;
        Integer offset = 0;
        String type = "expense";

        TransactionData transactionData = new TransactionData(year, month, limit, offset, type);

        assertEquals(year, transactionData.getYear());
        assertEquals(month, transactionData.getMonth());
        assertEquals(limit, transactionData.getLimit());
        assertEquals(offset, transactionData.getOffset());
    }
}
