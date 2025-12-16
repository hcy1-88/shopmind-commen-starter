package com.shopmind.framework.autoconfig;

import io.seata.spring.annotation.GlobalTransactionScanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * Seata 自动配置
 * 当 classpath 中存在 GlobalTransactionScanner 类时自动启用（即引入了 Seata 依赖）
 */
@Slf4j
@Configuration
@ConditionalOnClass(GlobalTransactionScanner.class)
public class SeataAutoConfiguration {

    @PostConstruct
    public void init() {
        log.info("Shopmind Seata 自动配置已启用");
    }
}
