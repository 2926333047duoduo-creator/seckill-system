package com.seckill.backend.common;

/**
 * Global constant messages used across the system.
 */
public class MessageConstants {

    // ====== Success ======
    public static final String REGISTER_SUCCESS = "注册成功";
    public static final String VOUCHER_CREATED_SUCCESS = "优惠券创建成功";
    public static final String VOUCHER_UPDATED_SUCCESS = "优惠券修改成功";
    public static final String VOUCHER_DELETED_SUCCESS = "优惠券删除成功";

    // ====== Failure ======
    public static final String ACCOUNT_ALREADY_EXISTS = "账号已存在，请更换一个";
    public static final String LOGIN_FAILED = "账号或密码错误";
    public static final String TOO_MANY_REQUESTS = "请求过于频繁，请稍后重试";
    public static final String ROLE_NOT_MATCH = "";

    // ====== Auth & Permission ======
    public static final String MISSING_TOKEN = "缺少令牌";
    public static final String INVALID_TOKEN = "无效令牌";
    public static final String TOKEN_EXPIRED = "登录状态已过期";
    public static final String FORBIDDEN_ACCESS = "无权限访问该接口";

    // ====== Voucher Module ======
    public static final String VOUCHER_NOT_FOUND = "未找到对应的优惠券";
    public static final String VOUCHER_DELETE_DENIED = "开始时间之后不可删除优惠券";
    public static final String VOUCHER_UPDATE_DENIED = "开始时间之后不可修改优惠券";
    public static final String VOUCHER_INVALID_UPDATE_FIELD = "仅允许修改优惠券名称和开始时间";
    public static final String VOUCHER_OPERATION_FAILED = "优惠券操作失败";

    // ====== Common / Server ======
    public static final String SERVER_ERROR = "服务器内部错误，请稍后再试";

    // ====== Voucher Order / Seckill ======
    public static final String OUT_OF_STOCK = "库存不足";
    public static final String DUPLICATE_ORDER = "不能重复下单";
}
