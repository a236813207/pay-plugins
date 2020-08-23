package com.ken.pay.modules.payment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ken.pay.common.BaseFillEntity;
import com.ken.pay.modules.payment.enums.PaymentMethod;
import com.ken.pay.modules.payment.enums.PaymentStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 支付流水表
 * </p>
 *
 * @author ken
 * @date 2020/07/10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("tb_payment")
public class Payment extends BaseFillEntity {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 支付金额
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 过期时间
     */
    @TableField("expire")
    private LocalDateTime expire;

    /**
     * 备注
     */
    @TableField("memo")
    private String memo;

    /**
     * 支付方式（1 在线支付 2 线下支付）
     */
    @TableField("method")
    private PaymentMethod method;

    /**
     * 操作人（后台结算使用）
     */
    @TableField("operator")
    private Integer operator;

    @TableField("order_id")
    private String orderId;

    /**
     * 支付时间
     */
    @TableField("pay_time")
    private LocalDateTime payTime;

    /**
     * 支付人账号信息
     */
    @TableField("payer")
    private String payer;

    /**
     * 支付方式
     */
    @TableField("payment_method")
    private String paymentMethod;

    /**
     * 支付插件
     */
    @TableField("payment_plugin_id")
    private String paymentPluginId;

    /**
     * 状态（1 等待支付 2 支付成功 3 支付失败）
     */
    @TableField("status")
    private PaymentStatus status;

    /**
     * 支付凭证
     */
    @TableField("trade_no")
    private String tradeNo;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private String userId;

}
