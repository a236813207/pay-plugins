package com.ken.pay.modules.payment.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 支付网站属性配置
 * </p>
 *
 * @author Ken
 * @date 2020/07/10
 */
@Data
@Accessors(chain = true)
@TableName("tb_payment_plugin_attribute")
public class PaymentPluginAttribute implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 支付网关ID
     */
    @TableId("payment_plugin_id")
    private Long paymentPluginId;

    /**
     * 属性值
     */
    @TableField("attributes")
    private String attributes;

    /**
     * 属性名称
     */
    @TableField("name")
    private String name;


}
