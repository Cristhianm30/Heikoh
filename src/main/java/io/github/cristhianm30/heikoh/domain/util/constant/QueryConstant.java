package io.github.cristhianm30.heikoh.domain.util.constant;

public class QueryConstant {
    public static final String SELECT_CATEGORY_AS_KEY_SUM_AMOUNT_AS_TOTAL_AMOUNT_FROM_EXPENSES_WHERE_USER_ID_GROUP_BY_CATEGORY = "SELECT category AS `key`, SUM(amount) AS total_amount FROM expenses WHERE user_id = :userId GROUP BY category";
    public static final String SELECT_CATEGORY_AS_KEY_SUM_AMOUNT_AS_TOTAL_AMOUNT_FROM_EXPENSES_WHERE_USER_ID_AND_TRANSACTION_DATE_BETWEEN_GROUP_BY_CATEGORY = "SELECT category AS `key`, SUM(amount) AS total_amount FROM expenses WHERE user_id = :userId AND transaction_date BETWEEN :startDate AND :endDate GROUP BY category";
    public static final String SELECT_PAYMENT_METHOD_AS_KEY_SUM_AMOUNT_AS_TOTAL_AMOUNT_FROM_EXPENSES_WHERE_USER_ID_GROUP_BY_PAYMENT_METHOD = "SELECT payment_method AS `key`, SUM(amount) AS total_amount FROM expenses WHERE user_id = :userId GROUP BY payment_method";
    public static final String SELECT_PAYMENT_METHOD_AS_KEY_SUM_AMOUNT_AS_TOTAL_AMOUNT_FROM_EXPENSES_WHERE_USER_ID_AND_TRANSACTION_DATE_BETWEEN_GROUP_BY_PAYMENT_METHOD = "SELECT payment_method AS `key`, SUM(amount) AS total_amount FROM expenses WHERE user_id = :userId AND transaction_date BETWEEN :startDate AND :endDate GROUP BY payment_method";

    public static final String SELECT_SUM_AMOUNT_FROM_EXPENSES_WHERE_USER_ID_AND_TRANSACTION_DATE_BETWEEN = "SELECT SUM(amount) FROM expenses WHERE user_id = :userId AND transaction_date BETWEEN :startDate AND :endDate";
    public static final String SELECT_SUM_AMOUNT_FROM_EXPENSES_WHERE_USER_ID = "SELECT SUM(amount) FROM expenses WHERE user_id = :userId";

    public static final String SELECT_SUM_AMOUNT_FROM_INCOMES_WHERE_USER_ID_AND_TRANSACTION_DATE_BETWEEN = "SELECT SUM(amount) FROM incomes WHERE user_id = :userId AND transaction_date BETWEEN :startDate AND :endDate";
    public static final String SELECT_SUM_AMOUNT_FROM_INCOMES_WHERE_USER_ID = "SELECT SUM(amount) FROM incomes WHERE user_id = :userId";

    public static final String CATEGORY_FIELD = "category";
    public static final String PAYMENT_METHOD_FIELD = "paymentMethod";

    public static final String KEY_ALIAS = "key";
    public static final String TOTAL_AMOUNT_ALIAS = "total_amount";

}
