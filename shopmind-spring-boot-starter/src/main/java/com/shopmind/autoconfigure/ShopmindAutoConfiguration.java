package com.shopmind.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;

/**
 * Shopmind 自动配置主类
 */
@Slf4j
@AutoConfiguration
@ComponentScan(basePackages = "com.shopmind")
public class ShopmindAutoConfiguration {

    @PostConstruct
    public void init() {
        log.info("========================================");
        log.info("Shopmind Common Starter 初始化完成");
        log.info("========================================");
    }
}
