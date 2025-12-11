package com.hcy.shopmind.common.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Redis/Redisson 自动配置
 * 仅在以下条件同时满足时生效：
 * 1. classpath 中存在 RedissonClient 类（即引入了 Redisson 依赖）
 * 2. 配置文件中设置了 shopmind.redis.enabled=true
 */
@Slf4j
@Configuration
@ConditionalOnClass(RedissonClient.class)
@ConditionalOnProperty(prefix = "shopmind.redis", name = "enabled", havingValue = "true", matchIfMissing = false)
public class RedisAutoConfiguration {

    @PostConstruct
    public void init() {
        log.info("Shopmind Redis/Redisson 自动配置已启用");
    }
}
