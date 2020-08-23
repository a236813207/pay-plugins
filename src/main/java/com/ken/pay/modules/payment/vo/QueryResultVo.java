package com.ken.pay.modules.payment.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author Ken
 * @date 2018/9/13.
 */
@ApiModel
public class QueryResultVo {
    public static final String SUCCESS = "00";
    public static final String TRADING = "01";
    public static final String FALI = "02";
    public static final String NOTPAY = "10";
    public static final String CLOSED = "11";

    /**
     * 状态
     */
    @ApiModelProperty("状态 00-成功  01-处理中  02-失败 10-未支付 11-已关闭")
    private String state;

    /**
     * 外部交易号
     */
    @JsonIgnore
    private String outTradeNo;

    @JsonIgnore
    private Object extra;

    @ApiModelProperty("失败原因")
    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Object getExtra() {
        return extra;
    }

    public void setExtra(Object extra) {
        this.extra = extra;
    }
}
