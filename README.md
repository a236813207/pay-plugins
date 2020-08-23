# pay-plugins
基于springboot、mybatis-plus可插拔的支付插件，已在线上多个项目实践

##使用流程：

1.在数据库 **tb_payment_plugin** 表配置好支付插件，现已实现3种支付插件：支付宝App支付、微信App支付、微信小程序支付，
表字段gateway的值对应继承AbstractPaymentPlugin类的组件Component中的值，采用Autowire注入到支付插件List中去

2.在数据库 **tb_payment_plugin_attribute** 表配置支付插件的属性，支付名称、appid、密钥等信息

3.前端在调用时先获取所有可用的支付插件，后台接口返回所有可用的支付插件信息，

4.前端提交支付时，传入选择支付插件id，后台提交预支付订单并返回前端调起支付app所需参数

5.前端调起支付app进行支付，后台回调接口等待支付回调，处理回调信息

##其他支付方式扩展：

1.数据库参照使用流程1，2进行数据库配置

2.代码中新的支付方式实现类继承AbstractPaymentPlugin类，实现以下接口：
##### doPay（预支付下单），
##### getNotifyMessage（回调成功返回信息），
##### verifyNotify（解析回调通知），
##### doQuery（订单查询），
##### getPluginName（数据库未配置的默认支付插件名称），
##### refund（退款），
##### getTimeout（支付超时），
##### getClientType（可使用的客户端）


