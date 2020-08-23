package com.ken.pay.modules.payment.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ken.pay.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 支付网关
 * </p>
 *
 * @author Ken
 * @date 2020/07/10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("tb_payment_plugin")
public class PaymentPlugin extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 顺序
     */
    @TableField("orders")
    private Integer orders;

    /**
     * 支付网关名称
     */
    @TableField("gateway")
    private String gateway;

    /**
     * 是否启用，安装后启用，卸载后禁用
     */
    @TableField("is_enabled")
    private Boolean isEnabled;


}
