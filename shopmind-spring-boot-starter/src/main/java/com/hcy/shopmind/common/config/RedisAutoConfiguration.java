package com.hcy.shopmind.common.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Redis/Redisson 自动配置
 * 当 classpath 中存在 RedissonClient 类时自动启用（即引入了 Redisson 依赖）
 */
@Slf4j
@Configuration
@ConditionalOnClass(RedissonClient.class)
public class RedisAutoConfiguration {

    @PostConstruct
    public void init() {
        log.info("Shopmind Redis/Redisson 自动配置已启用");
    }
}
