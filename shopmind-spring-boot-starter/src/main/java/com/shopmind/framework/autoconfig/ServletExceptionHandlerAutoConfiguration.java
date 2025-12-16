package com.shopmind.framework.autoconfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * 全局异常处理自动配置
 * 仅在 Web 应用中启用
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(
        prefix = "shopmind.exception-handler",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
@ComponentScan(basePackages = "com.shopmind.framework.exception.servlet")
public class ServletExceptionHandlerAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ServletExceptionHandlerAutoConfiguration.class);

    @PostConstruct
    public void init() {
        log.info("初始化全局异常处理器 -- FOR servlet APP");
    }
}
