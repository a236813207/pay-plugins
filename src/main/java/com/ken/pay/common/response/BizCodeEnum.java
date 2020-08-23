package com.ken.pay.common.response;

/**
 * 异常形式枚举基类
 * @author ken
 * @date 2017/12/8
 * @param <E> 枚举类型
 */
public interface BizCodeEnum<E extends Enum<E>> {


    int getCode();

    String getMessage();

    /**
     * 起始值
     */
    int getStart();
}
