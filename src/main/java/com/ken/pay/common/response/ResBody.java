package com.ken.pay.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Nullable;

/**
 * @author Ken
 * @date 2017/12/6.
 */
@ApiModel(value = "响应参数")
public class ResBody<T> {
    @ApiModelProperty(value = "响应代码:2接口调用成功、3权限授权过期")
    private Integer code;
    @ApiModelProperty(value = "提示信息")
    private String msg;
    @ApiModelProperty(value = "响应数据")
    private T data;
    @ApiModelProperty(value = "错误跟踪")
    private String trace;

    private ResBody() {
    }

    private ResBody(BizCodeEnum codeEnum) {
        this.code = codeEnum.getCode();
        this.msg = codeEnum.getMessage();
    }

    private ResBody(BizCodeEnum codeEnum, @Nullable T data) {
        this.code = codeEnum.getCode();
        this.msg = codeEnum.getMessage();
        this.data = data;
    }

    private ResBody(Integer code, String msg) {
        this(code, msg, null);
    }

    private ResBody(Integer code, String msg, @Nullable T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static ResBody success() {
        ErrorCode bizCode = ErrorCode.OK;
        return new ResBody(bizCode.getCode(), bizCode.getMessage());
    }

    public static <T> ResBody success(T data) {
        return success().data(data);
    }

    public static ResBody failure() {
        ErrorCode bizCode = ErrorCode.FAIL;
        return new ResBody(bizCode.getCode(), bizCode.getMessage());
    }

    public static <T> ResBody failure(BizCodeEnum codeEnum) {
        return new ResBody(codeEnum);
    }

    public static <T> ResBody failure(String message) {
        ErrorCode bizCode = ErrorCode.FAIL;
        return new ResBody(bizCode.getCode(), message);
    }

    public static <T> ResBody custom(int code, String msg) {
        return new ResBody(code, msg);
    }

    public ResBody message(String message) {
        this.msg = message;
        return this;
    }

    public ResBody code(Integer code) {
        this.code = code;
        return this;
    }

    public ResBody data(T data) {
        this.data = data;
        return this;
    }

    public ResBody trace(String trace) {
        this.trace = trace;
        return this;
    }

    public String getTrace() {
        return trace;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public static ResBody custom(BizCodeEnum bizCodeEnum) {
        return custom(bizCodeEnum.getCode(), bizCodeEnum.getMessage());
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
