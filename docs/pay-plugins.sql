-- ----------------------------
-- Table structure for `tb_payment`
-- ----------------------------
DROP TABLE IF EXISTS `tb_payment`;
CREATE TABLE `tb_payment` (
  `id` varchar(36) NOT NULL,
  `create_by` varchar(36) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_by` varchar(36) DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `amount` decimal(19,2) DEFAULT NULL COMMENT '支付金额',
  `expire` datetime DEFAULT NULL COMMENT '过期时间',
  `memo` varchar(200) DEFAULT NULL COMMENT '备注',
  `method` int(11) DEFAULT NULL COMMENT '支付方式（1 在线支付 2 线下支付）',
  `operator` int(11) DEFAULT NULL COMMENT '操作人（后台结算使用）',
  `order_id` varchar(40) DEFAULT NULL,
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `payer` varchar(60) DEFAULT NULL COMMENT '支付人账号信息',
  `payment_method` varchar(255) DEFAULT NULL COMMENT '支付方式名称',
  `payment_plugin_id` varchar(200) DEFAULT NULL COMMENT '支付插件',
  `status` int(11) DEFAULT NULL COMMENT '状态（1 等待支付 2 支付成功 3 支付失败）',
  `trade_no` varchar(42) DEFAULT NULL COMMENT '支付凭证',
  `user_id` varchar(64) DEFAULT NULL COMMENT '用户ID',
  PRIMARY KEY (`id`),
  KEY `idx_payment_1` (`order_id`) USING BTREE,
  KEY `idx_payment_2` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付流水表';

-- ----------------------------
-- Table structure for `tb_payment_plugin`
-- ----------------------------
DROP TABLE IF EXISTS `tb_payment_plugin`;
CREATE TABLE `tb_payment_plugin` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_by` varchar(36) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_by` varchar(36) DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `orders` int(11) DEFAULT NULL COMMENT '顺序',
  `gateway` varchar(200) DEFAULT NULL COMMENT '支付网关名称',
  `is_enabled` bit(1) DEFAULT NULL COMMENT '是否启用，安装后启用，卸载后禁用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COMMENT='支付网关';

-- ----------------------------
-- Records of tb_payment_plugin
-- ----------------------------
INSERT INTO `tb_payment_plugin` VALUES ('1', 'system', '2020-07-28 14:35:50', 'system', '2020-07-28 14:35:52', '1', 'aliAppPayPlugin', '');
INSERT INTO `tb_payment_plugin` VALUES ('2', 'system', '2020-07-28 14:37:47', 'system', '2020-07-28 14:37:49', '2', 'wxAppPayPlugin', '');
INSERT INTO `tb_payment_plugin` VALUES ('3', 'system', '2020-07-28 14:37:47', 'system', '2020-07-28 14:37:49', '3', 'wxMaPayPlugin', '');

-- ----------------------------
-- Table structure for `tb_payment_plugin_attribute`
-- ----------------------------
DROP TABLE IF EXISTS `tb_payment_plugin_attribute`;
CREATE TABLE `tb_payment_plugin_attribute` (
  `payment_plugin_id` bigint(20) NOT NULL COMMENT '支付网关ID',
  `name` varchar(100) NOT NULL COMMENT '属性名称',
  `attributes` text COMMENT '属性值',
  PRIMARY KEY (`payment_plugin_id`,`name`),
  CONSTRAINT `tb_payment_plugin_attribute_ibfk_1` FOREIGN KEY (`payment_plugin_id`) REFERENCES `tb_payment_plugin` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付网站属性配置';

-- ----------------------------
-- Records of tb_payment_plugin_attribute
-- ----------------------------
INSERT INTO `tb_payment_plugin_attribute` VALUES ('1', 'app_id', '88888888');
INSERT INTO `tb_payment_plugin_attribute` VALUES ('1', 'description', 'APP支付宝支付');
INSERT INTO `tb_payment_plugin_attribute` VALUES ('1', 'logo', 'http://xfbetterdb.oss-cn-hangzhou.aliyuncs.com/2f397ecc19de1bacbc6a6f5414d3a3a8.jpg');
INSERT INTO `tb_payment_plugin_attribute` VALUES ('1', 'paymentName', '支付宝');
INSERT INTO `tb_payment_plugin_attribute` VALUES ('1', 'private_key', '应用私钥');
INSERT INTO `tb_payment_plugin_attribute` VALUES ('1', 'public_key', '支付宝公钥');
INSERT INTO `tb_payment_plugin_attribute` VALUES ('2', 'appid', 'wx88888888');
INSERT INTO `tb_payment_plugin_attribute` VALUES ('2', 'description', 'APP微信支付');
INSERT INTO `tb_payment_plugin_attribute` VALUES ('2', 'keyPath', '/data/project/piano/apiclient_cert.p12');
INSERT INTO `tb_payment_plugin_attribute` VALUES ('2', 'logo', 'http://xfbetterdb.oss-cn-hangzhou.aliyuncs.com/eaab699800353b5dfa78d23c9b7a497f.png');
INSERT INTO `tb_payment_plugin_attribute` VALUES ('2', 'mchId', 'mchId');
INSERT INTO `tb_payment_plugin_attribute` VALUES ('2', 'mchkey', 'mchkey');
INSERT INTO `tb_payment_plugin_attribute` VALUES ('2', 'paymentName', '微信支付');
