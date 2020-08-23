package com.ken.pay.modules.payment.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

/**
 * 支付状态
 * @author Ken
 * @date 2020/7/10
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum PaymentStatus implements IEnum<Integer> {
    /**
     * 支付状态
     */
    WAIT(1, "等待支付"),
    SUCCESS(2, "支付成功"),
    FAILURE(3, "支付失败");

    private int value;
    private String desc;

    PaymentStatus(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

}
