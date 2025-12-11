package com.hcy.shopmind.common.config;

import com.alibaba.cloud.stream.binder.rocketmq.RocketMQMessageChannelBinder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * RocketMQ 自动配置
 * 仅在以下条件同时满足时生效：
 * 1. classpath 中存在 RocketMQMessageChannelBinder 类（即引入了 RocketMQ 依赖）
 * 2. 配置文件中设置了 shopmind.rocketmq.enabled=true
 */
@Slf4j
@Configuration
@ConditionalOnClass(RocketMQMessageChannelBinder.class)
@ConditionalOnProperty(prefix = "shopmind.rocketmq", name = "enabled", havingValue = "true", matchIfMissing = false)
public class RocketMQAutoConfiguration {

    @PostConstruct
    public void init() {
        log.info("Shopmind RocketMQ 自动配置已启用");
    }
}
