package com.shopmind.framework.autoconfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * Gateway 全局异常处理自动配置
 * 仅在 Reactive Web 应用且有 Spring Cloud Gateway 依赖时启用
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnProperty(
        prefix = "shopmind.exception-handler",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
@ComponentScan(basePackages = "com.shopmind.framework.exception.gateway")
public class GatewayExceptionHandlerAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(GatewayExceptionHandlerAutoConfiguration.class);

    @PostConstruct
    public void init() {
        log.info("初始化 Gateway 全局异常处理器");
    }
}