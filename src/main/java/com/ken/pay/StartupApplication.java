package com.ken.pay;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @author Ken
 * @date 2020/2/19
 */
@SpringBootApplication
@Slf4j
public class StartupApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(StartupApplication.class, args);
        printContextInfo(applicationContext);
    }

    private static void printContextInfo(ConfigurableApplicationContext context) {
        ConfigurableEnvironment environment = context.getEnvironment();

        // 项目名称
        String projectFinalName = environment.getProperty("info.project-finalName");
        // 项目版本
        String projectVersion = environment.getProperty("info.project-version");
        // 项目profile
        String profileActive = environment.getProperty("spring.profiles.active");
        // 项目路径
        String contextPath = environment.getProperty("server.servlet.context-path");
        // 项目端口
        String port = environment.getProperty("server.port");

        log.info("projectFinalName : {}", projectFinalName);
        log.info("projectVersion : {}", projectVersion);
        log.info("profileActive : {}", profileActive);
        log.info("contextPath : {}", contextPath);
        log.info("port : {}", port);
    }
}
