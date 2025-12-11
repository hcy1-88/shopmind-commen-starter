package com.hcy.shopmind.common.config;

import io.seata.spring.annotation.GlobalTransactionScanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Seata 自动配置
 * 仅在以下条件同时满足时生效：
 * 1. classpath 中存在 GlobalTransactionScanner 类（即引入了 Seata 依赖）
 * 2. 配置文件中设置了 shopmind.seata.enabled=true
 */
@Slf4j
@Configuration
@ConditionalOnClass(GlobalTransactionScanner.class)
@ConditionalOnProperty(prefix = "shopmind.seata", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SeataAutoConfiguration {

    @PostConstruct
    public void init() {
        log.info("Shopmind Seata 自动配置已启用");
    }
}
