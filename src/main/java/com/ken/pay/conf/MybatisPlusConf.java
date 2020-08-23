package com.ken.pay.conf;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Ken
 * @date 2020/02/08
 */
@Configuration
@MapperScan({"com.ken.pay.modules.**.mapper"})
public class MybatisPlusConf {

}
