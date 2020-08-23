package com.ken.pay.modules.payment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ken.pay.modules.payment.entity.Payment;
import com.ken.pay.modules.payment.vo.PaymentResultVo;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 支付流水表 服务类
 * </p>
 *
 * @author Ken
 * @date 2020/07/10
 */
public interface IPaymentService extends IService<Payment> {

    /**
     * 提交支付
     * @param userId 用户id
     * @param paymentId 生成的流水记录id
     * @param orderId 订单id
     * @param paymentPluginId 支付插件id
     * @param request HttpServletRequest
     * @return 返回支付流水id
     */
    PaymentResultVo submit(String userId, String paymentId, String orderId, String paymentPluginId, HttpServletRequest request);

    /**
     * 支付成功回调处理
     * @param payment 支付流水
     */
    void handle(Payment payment);

    /**
     * 支付更新流水状态
     * @param payment 支付流水
     * @return 返回更新后支付流水记录
     */
    Payment payment(Payment payment);
}
