package com.shopmind.framework.config;

import com.shopmind.framework.properties.ExceptionHandlerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.annotation.PostConstruct;

/**
 * 全局异常处理自动配置
 * 仅在 Web 应用中启用
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(RestControllerAdvice.class)
@ConditionalOnProperty(
        prefix = "shopmind.exception-handler",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
@EnableConfigurationProperties(ExceptionHandlerProperties.class)
@ComponentScan(basePackages = "com.shopmind.framework.exception")
public class ExceptionHandlerAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ExceptionHandlerAutoConfiguration.class);

    @PostConstruct
    public void init() {
        log.info("初始化全局异常处理器 GlobalExceptionHandler");
    }
}
