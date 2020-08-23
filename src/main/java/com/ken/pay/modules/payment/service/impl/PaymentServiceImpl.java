package com.ken.pay.modules.payment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ken.pay.modules.payment.entity.Payment;
import com.ken.pay.modules.payment.enums.PaymentMethod;
import com.ken.pay.modules.payment.enums.PaymentStatus;
import com.ken.pay.modules.payment.mapper.PaymentMapper;
import com.ken.pay.modules.payment.plugin.AbstractPaymentPlugin;
import com.ken.pay.modules.payment.service.IPaymentPluginService;
import com.ken.pay.modules.payment.service.IPaymentService;
import com.ken.pay.modules.payment.vo.PaymentResultVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * <p>
 * 支付流水表 服务实现类
 * </p>
 *
 * @author Ken
 * @date 2020/07/10
 */
@Service
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, Payment> implements IPaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private IPaymentPluginService pluginService;

    @Override
    public PaymentResultVo submit(String userId, String paymentId, String orderId, String paymentPluginId,
                                  HttpServletRequest request) {
        AbstractPaymentPlugin paymentPlugin = pluginService.getPaymentPlugin(paymentPluginId);
        Assert.isTrue(paymentPlugin != null && paymentPlugin.getIsEnabled(), "支付插件不可用");

        Payment payment = new Payment();

        payment.setId(paymentId);
        payment.setExpire(paymentPlugin.getTimeout() != null ? LocalDateTime.now().plusMinutes(paymentPlugin.getTimeout()) : null);
        payment.setMemo("订单支付");
        payment.setMethod(PaymentMethod.ONLINE);
        payment.setOrderId(orderId);
        payment.setPaymentMethod(paymentPlugin.getPaymentName());
        payment.setPaymentPluginId(paymentPluginId);
        payment.setStatus(PaymentStatus.WAIT);
        payment.setUserId(userId);
        this.save(payment);

        return paymentPlugin.sendPayRequest(payment.getId(), request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handle(Payment payment) {
        logger.debug("开始处理支付回调，流水号[{}]", payment.getId());
        Payment pay = this.payment(payment);
        if (pay == null) {
            return;
        }

        // 订单处理

        logger.debug("处理支付回调结束");
    }

    @Override
    public Payment payment(Payment payment) {
        String tradeNo = payment.getTradeNo();
        String payer = payment.getPayer();
        Payment pay = this.getById(payment.getId());

        // 如果已支付过，可能是多余的，忽略
        if (payment.getStatus() != PaymentStatus.WAIT) {
            return null;
        }
        pay.setTradeNo(tradeNo);
        pay.setStatus(PaymentStatus.SUCCESS);
        pay.setPayTime(LocalDateTime.now());
        pay.setPayer(payer);
        pay.setExpire(null);
        this.updateById(pay);

        return pay;
    }


    @Autowired
    public void setPluginService(IPaymentPluginService pluginService) {
        this.pluginService = pluginService;
    }


}
