package io.github.cristhianm30.heikoh.domain.util.constant;

public class PathConstant {
    public static final String API_V1 = "api/v1/";

    public static final String AUTH_BASE_PATH = API_V1 + "auth/";
    public static final String TRANSACTION_BASE_PATH = API_V1 + "transaction/";

    public static final String AUTH_REGISTER_ENDPOINT_PATH = "register";
    public static final String AUTH_LOGIN_ENDPOINT_PATH = "login";

    public static final String TRANSACTION_LIST_ENDPOINT_PATH = "list";
    public static final String TRANSACTION_DETAIL_ENDPOINT_PATH = "details/{transactionId}";
    public static final String TRANSACTION_CREATE_ENDPOINT_PATH = "{type}";
    public static final String TRANSACTION_UPDATE_ENDPOINT_PATH = "{type}/{transactionId}";
    public static final String TRANSACTION_DELETE_ENDPOINT_PATH = "{type}/{transactionId}";

    public static final String COMPLETE_ACTUATOR = "/actuator/**";
    public static final String DASHBOARD_BASE_PATH = API_V1 + "dashboard";
    public static final String DASHBOARD_SUMMARY_PATH = "/summary";
    public static final String DASHBOARD_EXPENSES_SUMMARY_BY_PATH = "/expenses/summary-by";
    public static final String DASHBOARD_INCOMES_SUMMARY_BY_PATH = "/incomes/summary-by";
    public static final String TRANSACTION_URI_FORMAT = API_V1 + "transactions/%s/%d";
}