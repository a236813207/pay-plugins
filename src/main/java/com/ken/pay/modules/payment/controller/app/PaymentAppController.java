package com.ken.pay.modules.payment.controller.app;

import com.google.common.collect.Lists;
import com.ken.pay.common.response.ResBody;
import com.ken.pay.modules.payment.entity.Payment;
import com.ken.pay.modules.payment.enums.ClientType;
import com.ken.pay.modules.payment.enums.PaymentStatus;
import com.ken.pay.modules.payment.plugin.AbstractPaymentPlugin;
import com.ken.pay.modules.payment.service.IPaymentPluginService;
import com.ken.pay.modules.payment.service.IPaymentService;
import com.ken.pay.modules.payment.vo.PaymentResultVo;
import com.ken.pay.modules.payment.vo.QueryResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 支付管理 app前端控制器
 * </p>
 *
 * @author ken
 * @since 2020/07/10
 */
@RestController
@RequestMapping("/app/payment")
@Api(value = "支付管理", tags = "支付管理")
public class PaymentAppController {

    private IPaymentPluginService pluginService;
    private IPaymentService paymentService;

    @GetMapping("/methods")
    @ApiOperation(value = "获取在线支付方式", responseContainer = "list")
    public ResBody<?> getPaymentPlugins(@RequestParam @ApiParam(value = "支付客户端类型", defaultValue = "app") ClientType clientType) {
        List<AbstractPaymentPlugin> plugins = this.pluginService.getPaymentPlugins(true, clientType.name());
        if (CollectionUtils.isEmpty(plugins)) {
            return ResBody.success(Lists.newArrayList());
        }
        List<Map<String,Object>> list = plugins.stream().map(item -> {
            Map<String, Object> result = new HashMap<>(4);
            result.put("id", item.getId());
            result.put("name", item.getPaymentName());
            result.put("logo", item.getLogo());
            result.put("description", item.getDescription());
            return result;
        }).collect(Collectors.toList());
        return ResBody.success(list);
    }

    @PostMapping("/submit")
    @ApiOperation(value = "提交支付", response = PaymentResultVo.class)
    public ResBody submit(@RequestParam String orderId,
                          @RequestParam String paymentPluginId,
                          @ApiIgnore HttpServletRequest request) {
        // todo 获取当前支付用户id
        String userId = "";
        // todo 生成支付流水记录id
        String paymentId = "";
        PaymentResultVo result = this.paymentService.submit(userId, paymentId, orderId, paymentPluginId, request);

        return ResBody.success(result);
    }

    /**
     * 异步通知
     *
     * @param sn 支付流水号
     * @return 状态
     */
    @RequestMapping("/notify/{sn}")
    @ApiIgnore
    public Object notify(@PathVariable String sn, HttpServletRequest request) {
        Payment payment = this.paymentService.getById(sn);
        if (payment != null) {
            AbstractPaymentPlugin plugin = this.pluginService.getPaymentPlugin(payment.getPaymentPluginId());
            if (plugin != null) {
                //如果订单已经支付完成
                if (payment.getStatus() == PaymentStatus.SUCCESS) {
                    return plugin.getNotifyMessage(sn, request);
                }
                if (plugin.verifyNotify(payment, request)) {
                    this.paymentService.handle(payment);
                    return plugin.getNotifyMessage(sn, request);
                }
            }
        }
        return "FAILURE";
    }

    @GetMapping("/query")
    @ApiOperation("支付状态查询")
    public ResBody<?> query(@ApiParam("支付流水号") @RequestParam String sn) {
        Payment payment = this.paymentService.getById(sn);
        if (payment != null) {
            AbstractPaymentPlugin plugin = this.pluginService.getPaymentPlugin(payment.getPaymentPluginId());
            if (plugin != null) {
                //如果订单已经支付完成
                if (payment.getStatus() == PaymentStatus.SUCCESS) {
                    QueryResultVo queryResult = new QueryResultVo();
                    queryResult.setState(QueryResultVo.SUCCESS);
                    queryResult.setExtra(payment);
                    return ResBody.success(queryResult);
                }
                //通过支付通道查询的结果
                QueryResultVo result = plugin.doQuery(payment.getId());
                //成功
                if (QueryResultVo.SUCCESS.equals(result.getState())) {
                    payment.setTradeNo(result.getOutTradeNo());
                    this.paymentService.handle(payment);
                }
                return ResBody.success(result);
            }
        }
        return ResBody.failure("未找到支付单");
    }

    @Autowired
    public void setPluginService(IPaymentPluginService pluginService) {
        this.pluginService = pluginService;
    }

    @Autowired
    public void setPaymentService(IPaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
