package com.seckill.backend.common;

public class MessageConstants {

    // 成功提示
    public static final String REGISTER_SUCCESS = "注册成功";

    // 失败提示
    public static final String ACCOUNT_ALREADY_EXISTS = "账号已存在，请更换一个";
    public static final String LOGIN_FAILED = "账号或密码错误";
    public static final String TOO_MANY_REQUESTS = "请求过于频繁，请稍后重试";

    // 鉴权与权限相关
    public static final String MISSING_TOKEN = "缺少令牌";
    public static final String INVALID_TOKEN = "无效令牌";
    public static final String TOKEN_EXPIRED = "登录状态已过期";
    public static final String FORBIDDEN_ACCESS = "无权限访问该接口";

    // 通用错误
    public static final String SERVER_ERROR = "服务器内部错误，请稍后再试";
}
