package com.ken.pay.common.response;


/**
 * @author Ken
 * @date 2017/12/6.
 */
public enum ErrorCode implements BizCodeEnum<ErrorCode> {
    /**
     * 错误码枚举
     */
    PARAM_ERROR(1, "接口参数错误"),
    OK(200, "接口调用成功"),
    PERMISSION_EXPIRED(3, "权限授权过期"),
    PERMISSION_DENIED(4,"接口权限不足"),
    FAIL(5, "服务器繁忙"),
    DATE_NULL(6, "数据异常"),
    AUTH_FAIL(7, "账号密码错误"),
    ACCOUNT_NO_EXISTIS(8, "账号不存在"),
    ACCOUNT_LOCKED(9, "账号被冻结");

    private int code;
    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public int getStart() {
        return 0;
    }
}
