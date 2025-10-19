package com.seckill.backend.context;
public class UserContext {

    private static final ThreadLocal<String> ACCOUNT_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<String> ROLE_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<String> USER_ID_HOLDER = new ThreadLocal<>();

    // ===== account =====
    public static void setAccount(String account) {
        ACCOUNT_HOLDER.set(account);
    }

    public static String getAccount() {
        return ACCOUNT_HOLDER.get();
    }

    // ===== role =====
    public static void setRole(String role) {
        ROLE_HOLDER.set(role);
    }

    public static String getRole() {
        return ROLE_HOLDER.get();
    }

    // ===== userId =====
    public static void setUserId(String userId) {
        USER_ID_HOLDER.set(userId);
    }

    public static String getUserId() {
        return USER_ID_HOLDER.get();
    }

    // ===== 清除线程变量 =====
    public static void clear() {
        ACCOUNT_HOLDER.remove();
        ROLE_HOLDER.remove();
        USER_ID_HOLDER.remove();
    }
}
