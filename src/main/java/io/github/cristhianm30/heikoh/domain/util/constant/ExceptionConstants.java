package io.github.cristhianm30.heikoh.domain.util.constant;

public class ExceptionConstants {

    public static final String UNEXPECTED_ERROR_TRY_AGAIN = "An unexpected error occurred. Please try again later.";
    public static final String USERNAME_ALREADY_IN_USE = "Username already in use";
    public static final String EMAIL_ALREADY_IN_USE = "Email already in use";
    public static final String ACCOUNT_IS_DISABLED = "The user account is disabled.";
    public static final String USER_NOT_FOUND = "User not found";
    public static final String INCOME_NOT_REGISTERED = "The income could not be registered.";
    public static final String EXPENSE_NOT_REGISTERED = "The expense could not be registered.";
    public static final String EXPENSE_NOT_FOUND = "Expense not found with id ";
    public static final String INCOME_NOT_FOUND = "Income not found with id ";
    public static final String INVALID_TRANSACTION_TYPE = "Invalid transaction type: ";
    public static final String INVALID_DATE_RANGE = "Start date cannot be after end date.";
    public static final String INVALID_DATE_FORMAT = "Invalid date format. Please use YYYY-MM-DD.";
    public static final String START_DATE_REQUIRED = "startDate is required.";
    public static final String END_DATE_REQUIRED = "endDate is required.";
    public static final String GROUP_BY_REQUIRED = "groupBy parameter is required.";
    public static final String INVALID_GROUP_BY_PARAMETER = "Invalid groupBy parameter. Must be 'category' or 'paymentMethod'.";
    public static final String QUERY_PARAM_TYPE_REQUIRED = "Query param 'type' is required.";
    public static final String INVALID_REQUEST_TYPE_FOR_TRANSACTION = "Invalid request type for transaction.";
    public static final String INVALID_TRANSACTION_TYPE_IN_PATH = "Invalid request type for transaction: ";


}
