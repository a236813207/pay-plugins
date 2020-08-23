package com.ken.pay.modules.payment.plugin;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.ken.pay.modules.payment.entity.Payment;
import com.ken.pay.modules.payment.enums.ClientType;
import com.ken.pay.modules.payment.enums.PaymentStatus;
import com.ken.pay.modules.payment.service.IPaymentService;
import com.ken.pay.modules.payment.vo.PaymentResultVo;
import com.ken.pay.modules.payment.vo.PluginAttributeVo;
import com.ken.pay.modules.payment.vo.QueryResultVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 支付宝支付插件
 * </p>
 *
 * @author ken
 * @date 2020/07/10
 */
@Component("aliAppPayPlugin")
public class AliAppPaymentPlugin extends AbstractPaymentPlugin {

    private static final Logger logger = LoggerFactory.getLogger(AliAppPaymentPlugin.class);

    private static final String ALIPAY_PUBLIC_KEY = "public_key";
    private static final String MERCHANT_PRIVATE_KEY = "private_key";
    private static final String APPID = "app_id";
    private static final String SERVER_URL = "https://openapi.alipay.com/gateway.do";

    @Autowired
    @Lazy
    private IPaymentService paymentService;

    private AlipayClient getClient() {
        String appId = getAttribute(APPID);
        String privateKey = getAttribute(MERCHANT_PRIVATE_KEY);
        String publicKey = getAttribute(ALIPAY_PUBLIC_KEY);
        return new DefaultAlipayClient(SERVER_URL, appId, privateKey, "json", "utf-8", publicKey, "RSA2");
    }

    @Override
    protected void doPay(PaymentResultVo result, String sn, HttpServletRequest request) {
        Payment payment = this.getPayment(sn);
        Assert.notNull(payment, "订单不存在");
        Assert.isTrue(payment.getStatus() != PaymentStatus.SUCCESS, "订单已支付");
        AlipayTradeAppPayRequest payRequest = new AlipayTradeAppPayRequest();
        //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setBody("订单支付");
        model.setSubject("订单支付");
        model.setOutTradeNo(sn);
        model.setTimeoutExpress("2h");
        model.setTotalAmount(payment.getAmount().toPlainString());
        model.setProductCode("QUICK_MSECURITY_PAY");
        payRequest.setBizModel(model);
        payRequest.setNotifyUrl(getNotifyUrl(sn));
        try {
            //这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeAppPayResponse response = getClient().sdkExecute(payRequest);
            if (response != null) {
                result.setExtra(response.getBody());
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getNotifyMessage(String sn, HttpServletRequest request) {
        return "success";
    }

    @Override
    public boolean verifyNotify(Payment payment, HttpServletRequest request) {
        //获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }

        try {
            boolean flag = AlipaySignature.rsaCheckV1(params, getAttribute(ALIPAY_PUBLIC_KEY), "utf-8", "RSA2");
            String outTradeNo = request.getParameter("out_trade_no");
            if (flag) {
                String tradeNo = request.getParameter("trade_no");
                //交易状态
                String tradeStatus = request.getParameter("trade_status");
                String totalAmount = request.getParameter("total_amount");
                BigDecimal payAmount = new BigDecimal(totalAmount);
                if (payAmount.compareTo(payment.getAmount()) != 0) {
                    logger.info("支付宝支付回调订单金额不相同，不处理。支付金额：[{}],订单金额：[{}]", payAmount, payment.getAmount());
                    return false;
                }
                //付款完成
                if ("TRADE_SUCCESS".equals(tradeStatus)) {
                    payment.setTradeNo(tradeNo);
                    return true;
                }
                //交易结束
                if ("TRADE_FINISHED".equals(tradeStatus)) {
                    return true;
                }
                logger.warn("支付宝回调未处理，订单号：[{}],订单状态：[{}]", outTradeNo, tradeStatus);
                return false;
            }
            String word = AlipaySignature.getSignCheckContentV1(params);
            logger.error("支付宝验证签名失败,签名:[{}],支付订单号:[{}]", word, outTradeNo);
            return false;
        } catch (AlipayApiException e) {
            logger.error("支付宝回调验签错误,code:[{}],msg:[{}]", e.getErrCode(), e.getErrMsg());
            return false;
        }
    }

    @Override
    public QueryResultVo doQuery(String sn) {
        Payment payment = this.getPayment(sn);
        Assert.notNull(payment, "订单不存在");
        Assert.isTrue(payment.getStatus() != PaymentStatus.SUCCESS, "订单已支付");
        QueryResultVo dto = new QueryResultVo();
        dto.setState(QueryResultVo.FALI);
        dto.setExtra(payment);
        try {
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            AlipayTradeQueryModel model = new AlipayTradeQueryModel();
            model.setOutTradeNo(sn);
            AlipayTradeQueryResponse response = getClient().execute(request);
            String tradeState = response.getTradeStatus();
            switch (tradeState) {
                case "TRADE_SUCCESS":
                    dto.setState(QueryResultVo.SUCCESS);
                    dto.setOutTradeNo(response.getOutTradeNo());
                    break;
                case "WAIT_BUYER_PAY":
                    dto.setState(QueryResultVo.TRADING);
                    break;
                case "TRADE_CLOSED":
                    dto.setState(QueryResultVo.FALI);
                    break;
                case "TRADE_FINISHED":
                    dto.setState(QueryResultVo.CLOSED);
                    break;
                default:
                    dto.setState(QueryResultVo.NOTPAY);
            }
        } catch (Exception e) {
            logger.error("查询错误,[{0}]", e);
        }
        return dto;
    }

    @Override
    protected List<PluginAttributeVo> getCustomSettingKeys() {
        List<PluginAttributeVo > list = new ArrayList<>();
        list.add(new PluginAttributeVo (APPID, "appid", true, "支付宝APPID"));
        list.add(new PluginAttributeVo (ALIPAY_PUBLIC_KEY, "public_key", true));
        list.add(new PluginAttributeVo (MERCHANT_PRIVATE_KEY, "private_key", true));
        return list;
    }

    @Override
    public String getPluginName() {
        return "支付宝支付(APP)";
    }

    @Override
    public void refund(String id, String outTradeNo, BigDecimal amount) {
        Payment payment = this.paymentService.getById(outTradeNo);
        if (payment == null) {
            throw new RuntimeException("未找到支付流水记录");
        }
        try {
            AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
            AlipayTradeRefundModel model = new AlipayTradeRefundModel();
            model.setOutTradeNo(outTradeNo);
            model.setRefundAmount(payment.getAmount().toPlainString());
            model.setOutRequestNo(id);
            request.setBizModel(model);
            AlipayTradeRefundResponse response = getClient().execute(request);
            if (!response.isSuccess()) {
                throw new RuntimeException("退款失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("退款失败");
        }
    }

    @Override
    public Integer getTimeout() {
        return 120;
    }

    @Override
    public String getClientType() {
        return ClientType.app.name();
    }
}
