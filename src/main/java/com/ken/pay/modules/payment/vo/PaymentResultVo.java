package com.ken.pay.modules.payment.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 支付回传前端参数
 * @author Ken
 * @date 2017/12/26.
 */
@Data
@Accessors(chain = true)
public class PaymentResultVo {

    /**
     * 支付插件ID
     */
    private String payPluginId;

    /**
     * 名称
     */
    private String paymentName;

    /**
     * 支付订单号
     */
    private String sn;

    /**
     * 额外参数（主要用于微信JSSDKPay）
     */
    private Object extra;

}
