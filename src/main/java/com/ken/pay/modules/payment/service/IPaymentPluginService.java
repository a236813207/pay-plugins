package com.ken.pay.modules.payment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ken.pay.modules.payment.entity.PaymentPlugin;
import com.ken.pay.modules.payment.plugin.AbstractPaymentPlugin;

import java.util.List;
import java.util.Map;

/**
 * @author Ken
 * @date 2019/12/22.
 */
public interface IPaymentPluginService  extends IService<PaymentPlugin> {

    /**
     * 获取可用的支付插件
     * @param isEnabled 是否可用
     * @param clientType 客户端类型
     * @return
     */
    List<AbstractPaymentPlugin> getPaymentPlugins(boolean isEnabled, String clientType);

    /**
     * 获取其中一个支付插件
     * @param id
     * @return
     */
    AbstractPaymentPlugin getPaymentPlugin(String id);

    /**
     * 根据网关获取支付插件
     * @param gateway
     * @return
     */
    PaymentPlugin findByGateway(String gateway);

    Map<String, String> getAttributes(Long pluginId);
}
