package com.ken.pay.modules.payment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ken.pay.modules.payment.entity.PaymentPlugin;
import com.ken.pay.modules.payment.entity.PaymentPluginAttribute;
import com.ken.pay.modules.payment.mapper.PaymentPluginAttributeMapper;
import com.ken.pay.modules.payment.mapper.PaymentPluginMapper;
import com.ken.pay.modules.payment.plugin.AbstractPaymentPlugin;
import com.ken.pay.modules.payment.service.IPaymentPluginService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 支付插件网关 服务实现类
 * </p>
 *
 * @author Ken
 * @date 2020/07/10
 */
@Service
public class PaymentPluginServiceIml extends ServiceImpl<PaymentPluginMapper, PaymentPlugin> implements IPaymentPluginService {

    @Autowired
    private List<AbstractPaymentPlugin> abstractPaymentPlugins = new ArrayList<>();
    @Autowired
    private Map<String, AbstractPaymentPlugin> paymentPluginMap = new HashMap<>();

    private PaymentPluginAttributeMapper pluginAttributeMapper;

    @Override
    public List<AbstractPaymentPlugin> getPaymentPlugins(final boolean isEnabled, String clientType) {
        return abstractPaymentPlugins.stream()
                .filter(item -> matchClientType(item.getClientType(), clientType) && item.getIsEnabled() == isEnabled)
                .sorted()
                .collect(Collectors.toList());
    }

    private boolean matchClientType(String expr, String clientType) {
        if (expr == null || "*".equals(expr)) {
            return true;
        }
        if (expr.equals(clientType)) {
            return true;
        }
        String[] split = StringUtils.split(expr, "|");
        return split != null && split.length > 0 && ArrayUtils.contains(split, clientType);
    }

    @Override
    public AbstractPaymentPlugin getPaymentPlugin(String id) {
        return paymentPluginMap.get(id);
    }

    @Override
    public PaymentPlugin findByGateway(String gateway) {
        return this.getOne(new QueryWrapper<PaymentPlugin>().lambda().eq(PaymentPlugin::getGateway, gateway).last("limit 1"));
    }

    @Override
    public Map<String, String> getAttributes(Long pluginId) {
        List<PaymentPluginAttribute> attributes = this.pluginAttributeMapper.selectList(
                new QueryWrapper<PaymentPluginAttribute>().lambda().eq(PaymentPluginAttribute::getPaymentPluginId, pluginId));
        if(attributes == null){
            return null;
        }
        Map<String,String> map = new HashMap<>(attributes.size());
        for (PaymentPluginAttribute attribute : attributes) {
            map.put(attribute.getName(),attribute.getAttributes());
        }
        return map;
    }

    @Autowired
    public void setPluginAttributeMapper(PaymentPluginAttributeMapper pluginAttributeMapper) {
        this.pluginAttributeMapper = pluginAttributeMapper;
    }
}
