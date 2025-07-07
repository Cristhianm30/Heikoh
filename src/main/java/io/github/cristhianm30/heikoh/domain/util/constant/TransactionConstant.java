package io.github.cristhianm30.heikoh.domain.util.constant;

import java.time.LocalDate;

public class TransactionConstant {
    public static final String TYPE_INCOME = "income";
    public static final String TYPE_EXPENSE = "expense";
    public static final int DEFAULT_START_MONTH = 1;
    public static final int DEFAULT_END_MONTH = 12;
    public static final LocalDate EARLIEST_DATE = LocalDate.MIN;
    public static final long DEFAULT_LIMIT = Long.MAX_VALUE;
    public static final int DEFAULT_OFFSET = 0;
    public static final int DEFAULT_END_DAY = 31;
    public static final int DEFAULT_START_DAY = 1;

}
