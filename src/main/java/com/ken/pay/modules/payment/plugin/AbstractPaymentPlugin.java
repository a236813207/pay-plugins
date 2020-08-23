package com.ken.pay.modules.payment.plugin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ken.pay.modules.payment.entity.Payment;
import com.ken.pay.modules.payment.entity.PaymentPlugin;
import com.ken.pay.modules.payment.service.IPaymentPluginService;
import com.ken.pay.modules.payment.service.IPaymentService;
import com.ken.pay.modules.payment.vo.PaymentResultVo;
import com.ken.pay.modules.payment.vo.PluginAttributeVo;
import com.ken.pay.modules.payment.vo.QueryResultVo;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 支付插件抽象类
 * </p>
 *
 * @author ken
 * @date 2020/07/10
 */
public abstract class AbstractPaymentPlugin implements Comparable<AbstractPaymentPlugin> {

    public static final String REQUEST_OPENID_KEY = "REQUEST_OPENID_KEY";

    public enum FeeType {
        //按百分比收费
        scale,
        //固定收费
        fixed
    }

    /**
     * 支付方式名称属性名称
     */
    private static final String PAYMENT_NAME_ATTRIBUTE_NAME = "paymentName";

    /**
     * 手续费类型属性名称
     */
    public static final String FEE_TYPE_ATTRIBUTE_NAME = "feeType";

    /**
     * 手续费属性名称
     */
    public static final String FEE_ATTRIBUTE_NAME = "fee";
    /**
     * LOGO属性名称
     */
    private static final String LOGO_ATTRIBUTE_NAME = "logo";

    /**
     * 描述属性名称
     */
    private static final String DESCRIPTION_ATTRIBUTE_NAME = "description";

    @Autowired
    private IPaymentPluginService pluginService;
    @Autowired
    private IPaymentService paymentService;

    @Value("${pay.site.url}")
    private String siteUrl;

    public final String getId() {
        return getClass().getAnnotation(Component.class).value();
    }


    /**
     * 支付动作
     * @param sn 支付流失记录id
     * @return 返回支付参数
     */
    public PaymentResultVo sendPayRequest(String sn, HttpServletRequest request) {
        PaymentResultVo result = new PaymentResultVo();
        result.setPaymentName(getPaymentName());
        result.setPayPluginId(getId());
        result.setSn(sn);
        doPay(result, sn, request);
        return result;
    }

    protected void doPay(PaymentResultVo result, String sn, HttpServletRequest request) {
    }

    /**
     * 获取通知返回消息
     * @param sn      支付单号
     * @param request httpServletrRequest
     * @return 获取通知
     */
    public abstract String getNotifyMessage(String sn, HttpServletRequest request);

    /**
     * 解析支付通知
     * @param payment 支付流水
     * @param request request
     * @return boolean
     */
    public abstract boolean verifyNotify(Payment payment, HttpServletRequest request);

    /**
     * 支付结果查询
     * @param sn 支付流水记录id
     * @return 查询结构
     */
    public abstract QueryResultVo doQuery(String sn);

    /**
     * 查询收款单
     *
     * @param sn 支付单号
     * @return 收款单
     */
    protected Payment getPayment(String sn) {
        return paymentService.getById(sn);
    }

    /**
     * 获取支付配置信息
     * @return 支付配置信息
     */
    protected abstract List<PluginAttributeVo> getCustomSettingKeys();

    @JsonIgnore
    public List<PluginAttributeVo> getSettingKeys() {
        List<PluginAttributeVo > list = new ArrayList<>();
        list.add(new PluginAttributeVo (PAYMENT_NAME_ATTRIBUTE_NAME, "名称", true));
        list.add(new PluginAttributeVo (DESCRIPTION_ATTRIBUTE_NAME, "描述"));
        list.add(new PluginAttributeVo (LOGO_ATTRIBUTE_NAME, "logo"));
        list.add(new PluginAttributeVo ("orders", "排序号", true));
        List<PluginAttributeVo> customSettingKeys = getCustomSettingKeys();
        if (!CollectionUtils.isEmpty(customSettingKeys)) {
            list.addAll(customSettingKeys);
        }
        for (PluginAttributeVo settingKey : list) {
            String attribute = getAttribute(settingKey.getName());
            if (attribute != null) {
                settingKey.setValue(attribute);
            }
            if ("orders".equals(settingKey.getName())) {
                Integer orders = getPluginConfig().getOrders();
                settingKey.setValue(orders == null ? "1" : orders + "");
            }
        }
        return list;
    }

    /**
     * 获取是否已安装
     * @return 是否已安装
     */
    public boolean getIsInstalled() {
        return this.pluginService.count(new QueryWrapper<PaymentPlugin>().lambda().eq(PaymentPlugin::getGateway, getId())) > 0;
    }

    /**
     * 获取插件配置信息
     * @return 插件配置
     */
    @JsonIgnore
    public PaymentPlugin getPluginConfig() {
        return this.pluginService.findByGateway(getId());
    }

    /**
     * 获取相关支付密钥等信息
     * @return 插件配置
     */
    private Map<String, String> getSettings() {
        PaymentPlugin pluginConfig = getPluginConfig();
        return this.pluginService.getAttributes(pluginConfig.getId());
    }

    /**
     * 获取排序号
     * @return 排序号
     */
    public Integer getOrder() {
        PaymentPlugin pluginConfig = getPluginConfig();
        Integer orders = pluginConfig != null ? pluginConfig.getOrders() : null;
        if (orders == null) {
            return 1;
        }
        return orders;
    }


    /**
     * 获取支付方式名称
     * @return 支付方式名称
     */
    public String getPaymentName() {
        PaymentPlugin pluginConfig = getPluginConfig();
        String name = pluginConfig != null ? getAttribute(PAYMENT_NAME_ATTRIBUTE_NAME) : null;
        if (StringUtils.isEmpty(name)) {
            return getPluginName();
        }
        return name;
    }

    public abstract String getPluginName();

    /**
     * 获取描述
     * @return 描述
     */
    public String getDescription() {
        PaymentPlugin pluginConfig = getPluginConfig();
        return pluginConfig != null ? getAttribute(DESCRIPTION_ATTRIBUTE_NAME) : null;
    }

    /**
     * 退款
     * @param id         退款/退货单号
     * @param outTradeNo 关联订单支付流水号
     * @param amount     退款金额
     */
    public abstract void refund(String id, String outTradeNo, BigDecimal amount);

    /**
     * 根据key获取属性值
     * @param key key
     * @return 属性值
     */
    public String getAttribute(String key) {
        Map<String, String> settings = getSettings();
        if (settings != null && key != null) {
            return settings.get(key);
        }
        return null;
    }

    /**
     * 是否可用
     * @return boolean
     */
    public boolean getIsEnabled() {
        PaymentPlugin pluginConfig = getPluginConfig();
        return pluginConfig != null && pluginConfig.getIsEnabled() != null && pluginConfig.getIsEnabled();
    }

    /**
     * 是否用于充值
     * @return boolean
     */
    public boolean isUseInpour() {
        return false;
    }

    /**
     * 获取LOGO
     * @return LOGO
     */
    public String getLogo() {
        PaymentPlugin pluginConfig = getPluginConfig();
        return pluginConfig != null ? this.getAttribute(LOGO_ATTRIBUTE_NAME) : null;
    }

    /**
     * 计算支付金额
     * @param amount 计算前的金额
     * @return 支付金额
     */
    public BigDecimal calculateAmount(BigDecimal amount) {
        return amount.add(calculateFee(amount)).setScale(2, RoundingMode.UP);
    }

    /**
     * 获取手续费
     * @return 手续费
     */
    public BigDecimal getFee() {
        PaymentPlugin pluginConfig = getPluginConfig();
        return pluginConfig != null ? new BigDecimal(this.getAttribute(FEE_ATTRIBUTE_NAME)) : null;
    }

    /**
     * 计算支付手续费
     * @return 支付手续费
     */
    public BigDecimal calculateFee(BigDecimal amount) {
        if (isCharge()) {
            BigDecimal fee;
            if (getFeeType() == FeeType.scale) {
                fee = amount.multiply(getFee());
            } else {
                fee = getFee();
            }
            return fee;
        }
        return new BigDecimal(0);
    }

    /**
     * 手续费类型
     * @return 手续费类型
     */
    public FeeType getFeeType() {
        PaymentPlugin pluginConfig = getPluginConfig();
        if (pluginConfig == null) {
            return null;
        }
        return FeeType.valueOf(this.getAttribute(FEE_TYPE_ATTRIBUTE_NAME));
    }

    /**
     * 是否收手续费
     * @return boolean
     */
    public boolean isCharge() {
        return false;
    }

    /**
     * 支付超时时长（分钟）
     * @return 超时时长
     */
    public abstract Integer getTimeout();

    /**
     * 支付客户端
     * @return 支付客户端科学
     */
    public abstract String getClientType();

    /**
     * 获取支付回调通知接口地址
     * @param sn 流水记录id
     * @return 支付回调接口地址
     */
    protected String getNotifyUrl(String sn) {
        return siteUrl + "/app/payment/notify/" + sn;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        AbstractPaymentPlugin other = (AbstractPaymentPlugin) obj;
        return new EqualsBuilder().append(getId(), other.getId()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getId()).toHashCode();
    }

    @Override
    public int compareTo(AbstractPaymentPlugin paymentPlugin) {
        return new CompareToBuilder().append(getOrder(), paymentPlugin.getOrder()).append(getId(), paymentPlugin.getId()).toComparison();
    }

}
