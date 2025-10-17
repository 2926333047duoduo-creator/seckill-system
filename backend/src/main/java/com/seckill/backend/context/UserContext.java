package com.seckill.backend.context;

public class UserContext {
    private static final ThreadLocal<String> ACCOUNT_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<String> ROLE_HOLDER = new ThreadLocal<>();

    public static void setAccount(String account) {
        ACCOUNT_HOLDER.set(account);
    }

    public static String getAccount() {
        return ACCOUNT_HOLDER.get();
    }

    public static void setRole(String role) {
        ROLE_HOLDER.set(role);
    }

    public static String getRole() {
        return ROLE_HOLDER.get();
    }

    public static void clear() {
        ACCOUNT_HOLDER.remove();
        ROLE_HOLDER.remove();
    }
}
