package com.ken.pay.modules.payment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author Ken
 * @date 2018/4/18.
 */
@ApiModel
public class PluginAttributeVo {

    @ApiModelProperty("属性name")
    private String name;
    @ApiModelProperty("属性名")
    private String text;
    @ApiModelProperty("属性value")
    private String value;
    @ApiModelProperty("是否必填")
    private Boolean require = Boolean.FALSE;
    @ApiModelProperty("备注内容")
    private String description;

    public PluginAttributeVo() { }

    public PluginAttributeVo(String name, String text) {
        this.name = name;
        this.text = text;
    }

    public PluginAttributeVo(String name, String text, Boolean require) {
        this.name = name;
        this.text = text;
        this.require = require;
    }

    public PluginAttributeVo(String name, String text, String description) {
        this.name = name;
        this.text = text;
        this.description = description;
    }

    public PluginAttributeVo(String name, String text, Boolean require, String description) {
        this.name = name;
        this.text = text;
        this.require = require;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getRequire() {
        return require;
    }

    public void setRequire(Boolean require) {
        this.require = require;
    }
}
