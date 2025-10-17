package com.seckill.backend.context;

public class UserContext {
    private static final ThreadLocal<String> THREAD_LOCAL = new ThreadLocal<>();

    public static void setAccount(String account) {
        THREAD_LOCAL.set(account);
    }

    public static String getAccount() {
        return THREAD_LOCAL.get();
    }

    public static void clear() {
        THREAD_LOCAL.remove();
    }
}
