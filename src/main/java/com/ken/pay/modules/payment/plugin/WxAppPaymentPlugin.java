package com.ken.pay.modules.payment.plugin;

import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.order.WxPayAppOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.ken.pay.modules.payment.entity.Payment;
import com.ken.pay.modules.payment.enums.ClientType;
import com.ken.pay.modules.payment.enums.PaymentStatus;
import com.ken.pay.modules.payment.service.IPaymentService;
import com.ken.pay.modules.payment.vo.PaymentResultVo;
import com.ken.pay.util.IpUtils;
import com.ken.pay.util.MoneyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 微信App支付插件
 * </p>
 *
 * @author Ken
 * @date 2020/07/10
 */
@Component("wxAppPayPlugin")
public class WxAppPaymentPlugin extends WxMaPaymentPlugin {

    @Autowired
    private IPaymentService paymentService;

    @Override
    protected void doPay(PaymentResultVo result, String sn, HttpServletRequest request) {
        Payment payment = this.getPayment(sn);
        Assert.notNull(payment, "订单不存在");
        Assert.isTrue(payment.getStatus() != PaymentStatus.SUCCESS, "订单已支付");
        WxPayUnifiedOrderRequest prepayInfo = WxPayUnifiedOrderRequest.newBuilder()
                .outTradeNo(sn)
                .totalFee(payment.getAmount().multiply(new BigDecimal(100)).intValue())
                .body("订单支付")
                .tradeType(WxPayConstants.TradeType.APP)
                .spbillCreateIp(IpUtils.getIp(request))
                .notifyUrl(getNotifyUrl(sn))
                .build();
        try {
            WxPayAppOrderResult payInfo = getWxPayService().createOrder(prepayInfo);
            if (payInfo != null) {
                Map<String, String> extra = new HashMap<>(8);
                extra.put("appid", payInfo.getAppId());
                extra.put("timestamp", payInfo.getTimeStamp());
                extra.put("noncestr", payInfo.getNonceStr());
                extra.put("partnerid", payInfo.getPartnerId());
                extra.put("prepayid", payInfo.getPrepayId());
                extra.put("package", payInfo.getPackageValue());
                extra.put("sign", payInfo.getSign());
                result.setExtra(extra);
            }
        } catch (WxPayException e) {
            throw new RuntimeException("生成JSSDKPayInfo失败");
        }
    }

    @Override
    public void refund(String id, String outTradeNo, BigDecimal amount) {
        Payment payment = this.paymentService.getById(outTradeNo);
        if (payment == null) {
            throw new RuntimeException("未找到支付流水记录");
        }
        WxPayRefundRequest wxPayRefundRequest = new WxPayRefundRequest();
        wxPayRefundRequest.setOutTradeNo(outTradeNo);
        wxPayRefundRequest.setOutRefundNo(id);
        wxPayRefundRequest.setTotalFee(MoneyUtils.toFee(payment.getAmount()));
        wxPayRefundRequest.setRefundFee(MoneyUtils.toFee(amount));
        try {
            getWxPayService().refund(wxPayRefundRequest);
        } catch (WxPayException e) {
            e.printStackTrace();
            throw new RuntimeException("退款失败");
        }
    }

    @Override
    public String getNotifyMessage(String sn, HttpServletRequest request) {
        return WxPayNotifyResponse.success("OK");
    }

    @Override
    public String getPluginName() {
        return "微信支付(APP)";
    }

    @Override
    public String getClientType() {
        return ClientType.app.name();
    }

}
