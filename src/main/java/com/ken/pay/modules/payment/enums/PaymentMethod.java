package com.ken.pay.modules.payment.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

/**
 * 支付方式
 * @author Ken
 * @date 2020/7/10
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum PaymentMethod implements IEnum<Integer> {
    /**
     * 支付方式
     */
    ONLINE(1, "线上支付"),
    OFFLINE(2, "线下支付");

    private int value;
    private String desc;

    PaymentMethod(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

}
