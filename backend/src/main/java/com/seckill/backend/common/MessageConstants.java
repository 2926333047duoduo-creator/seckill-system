package com.seckill.backend.common;

/**
 * Global constant messages used across the system.
 */
public class MessageConstants {

    // ====== Success ======
    public static final String REGISTER_SUCCESS = "Registration successful";
    public static final String VOUCHER_CREATED_SUCCESS = "Voucher created successfully";
    public static final String VOUCHER_UPDATED_SUCCESS = "Voucher updated successfully";
    public static final String VOUCHER_DELETED_SUCCESS = "Voucher deleted successfully";

    // ====== Failure ======
    public static final String ACCOUNT_ALREADY_EXISTS = "Account already exists, please use another one";
    public static final String LOGIN_FAILED = "Incorrect account or password";
    public static final String TOO_MANY_REQUESTS = "Too many requests, please try again later";
    public static final String ROLE_NOT_MATCH = "User role does not match permission requirements";

    // ====== Auth & Permission ======
    public static final String MISSING_TOKEN = "Missing authentication token";
    public static final String INVALID_TOKEN = "Invalid authentication token";
    public static final String TOKEN_EXPIRED = "Login session has expired";
    public static final String FORBIDDEN_ACCESS = "Access denied: insufficient permissions";

    // ====== Voucher Module ======
    public static final String VOUCHER_NOT_FOUND = "Voucher not found";
    public static final String VOUCHER_DELETE_DENIED = "Cannot delete voucher after its start time";
    public static final String VOUCHER_UPDATE_DENIED = "Cannot update voucher after its start time";
    public static final String VOUCHER_INVALID_UPDATE_FIELD = "Only voucher name and start time can be updated";
    public static final String VOUCHER_OPERATION_FAILED = "Voucher operation failed";

    // ====== Common / Server ======
    public static final String SERVER_ERROR = "Internal server error, please try again later";

    // ====== Voucher Order / Seckill ======
    public static final String OUT_OF_STOCK = "Out of stock";
    public static final String DUPLICATE_ORDER = "Duplicate order is not allowed";
}
