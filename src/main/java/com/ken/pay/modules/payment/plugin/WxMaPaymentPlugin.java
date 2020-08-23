package com.ken.pay.modules.payment.plugin;

import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryResult;
import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.ken.pay.modules.payment.entity.Payment;
import com.ken.pay.modules.payment.enums.ClientType;
import com.ken.pay.modules.payment.enums.PaymentStatus;
import com.ken.pay.modules.payment.service.IPaymentService;
import com.ken.pay.modules.payment.vo.PaymentResultVo;
import com.ken.pay.modules.payment.vo.PluginAttributeVo;
import com.ken.pay.modules.payment.vo.QueryResultVo;
import com.ken.pay.util.IpUtils;
import com.ken.pay.util.MoneyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 微信小程序支付插件
 * </p>
 *
 * @author Ken
 * @date 2020/07/10
 */
@Component("wxMaPayPlugin")
@Slf4j
public class WxMaPaymentPlugin extends AbstractPaymentPlugin {
    static final String KEY_PATH = "keyPath";
    static final String MCHKEY = "mchkey";
    static final String MCH_ID = "mchId";
    static final String APPID = "appid";

    private IPaymentService paymentService;

    @Override
    public String getPluginName() {
        return "微信支付(小程序)";
    }

    @Override
    public String getNotifyMessage(String sn, HttpServletRequest request) {
        return WxPayNotifyResponse.success("OK");
    }

    @Override
    public boolean verifyNotify(Payment payment, HttpServletRequest request) {
        try {
            WxPayService payService = getWxPayService();
            BufferedReader reader = request.getReader();
            StringBuilder inputString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                inputString.append(line);
            }
            log.debug("notify:{}", inputString.toString());
            WxPayOrderNotifyResult result = payService.parseOrderNotifyResult(inputString.toString());
            payment.setPayer(result.getOpenid());
            payment.setTradeNo(result.getTransactionId());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void doPay(PaymentResultVo result, String sn, HttpServletRequest request) {
        Payment payment = this.getPayment(sn);
        Assert.notNull(payment, "订单不存在");
        Assert.isTrue(payment.getStatus() != PaymentStatus.SUCCESS, "订单已支付");
        String openId = request.getAttribute(REQUEST_OPENID_KEY) + "";
        WxPayUnifiedOrderRequest prepayInfo = WxPayUnifiedOrderRequest.newBuilder()
                .openid(openId)
                .outTradeNo(sn)
                .totalFee(payment.getAmount()
                        .multiply(new BigDecimal(100)).intValue())
                .body("订单支付")
                .tradeType(WxPayConstants.TradeType.JSAPI)
                .spbillCreateIp(IpUtils.getIp(request))
                .notifyUrl(getNotifyUrl(sn))
                .build();
        try {
            WxPayMpOrderResult payInfo = getWxPayService().createOrder(prepayInfo);
            if (payInfo != null) {
                Map<String, String> extra = new HashMap<>();
                extra.put("appId", payInfo.getAppId());
                extra.put("timeStamp", payInfo.getTimeStamp());
                extra.put("nonceStr", payInfo.getNonceStr());
                extra.put("package", payInfo.getPackageValue());
                extra.put("signType", payInfo.getSignType());
                extra.put("paySign", payInfo.getPaySign());
                result.setExtra(extra);
            }
        } catch (WxPayException e) {
            throw new RuntimeException("支付失败");
        }
    }

    @Override
    public QueryResultVo doQuery(String sn) {
        Payment payment = this.getPayment(sn);
        Assert.notNull(payment, "订单不存在");
        Assert.isTrue(payment.getStatus() != PaymentStatus.SUCCESS, "订单已支付");
        QueryResultVo vo = new QueryResultVo();
        vo.setState(QueryResultVo.FALI);
        vo.setExtra(payment);
        try {
            WxPayOrderQueryResult queryResult = this.getWxPayService().queryOrder(null, sn);
            String tradeState = queryResult.getTradeState();
            switch (tradeState) {
                case "SUCCESS":
                    vo.setState(QueryResultVo.SUCCESS);
                    vo.setOutTradeNo(queryResult.getTransactionId());
                    break;
                case "USERPAYING":
                    vo.setState(QueryResultVo.TRADING);
                    break;
                case "NOTPAY":
                    vo.setState(QueryResultVo.NOTPAY);
                    break;
                case "PAYERROR":
                    vo.setState(QueryResultVo.FALI);
                    break;
                case "CLOSED":
                    vo.setState(QueryResultVo.CLOSED);
                    break;
                default:
                    break;
            }
        } catch (WxPayException e) {
            log.error("查询错误,[{0}]", e);
        }
        return vo;
    }

    @Override
    protected List<PluginAttributeVo> getCustomSettingKeys() {
        List<PluginAttributeVo > list = new ArrayList<>();
        list.add(new PluginAttributeVo (APPID, "appid", true, "小程序id"));
        list.add(new PluginAttributeVo (MCH_ID, "mchid", true));
        list.add(new PluginAttributeVo (MCHKEY, "mchKey", true));
        list.add(new PluginAttributeVo (KEY_PATH, "证书", true));
        return list;
    }

    protected WxPayService getWxPayService() {
        WxPayConfig payConfig = getPayConfig();
        WxPayService payService = new WxPayServiceImpl();
        payService.setConfig(payConfig);
        return payService;
    }

    private WxPayConfig getPayConfig() {
        WxPayConfig config = new WxPayConfig();
        config.setAppId(getAttribute(APPID));
        config.setMchId(getAttribute(MCH_ID));
        config.setMchKey(getAttribute(MCHKEY));
        config.setKeyPath(getAttribute(KEY_PATH));
        return config;
    }

    @Override
    public void refund(String id, String outTradeNo, BigDecimal amount) {
        Payment payment = this.paymentService.getById(outTradeNo);
        if (payment == null) {
            throw new RuntimeException("未找到流水记录");
        }
        WxPayRefundRequest wxPayRefundRequest = new WxPayRefundRequest();
        wxPayRefundRequest.setOutTradeNo(outTradeNo);
        wxPayRefundRequest.setOutRefundNo(id);
        wxPayRefundRequest.setTotalFee(MoneyUtils.toFee(payment.getAmount()));
        wxPayRefundRequest.setRefundFee(MoneyUtils.toFee(amount));
        try {
            WxPayRefundResult result = getWxPayService().refund(wxPayRefundRequest);
            if (result.getResultCode().equals("FAIL")) {
                throw new RuntimeException("退款失败");
            }
        } catch (WxPayException e) {
            e.printStackTrace();
            throw new RuntimeException("退款失败");
        }
    }

    @Override
    public Integer getTimeout() {
        return null;
    }

    @Override
    public String getClientType() {
        return ClientType.ma.name();
    }


    @Autowired
    public void setPaymentService(IPaymentService paymentService) {
        this.paymentService = paymentService;
    }

}
