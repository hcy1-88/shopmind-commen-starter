package com.shopmind.framework.autoconfig;

import com.alibaba.cloud.stream.binder.rocketmq.RocketMQMessageChannelBinder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * RocketMQ 自动配置
 * 当 classpath 中存在 RocketMQMessageChannelBinder 类时自动启用（即引入了 RocketMQ 依赖）
 */
@Slf4j
@Configuration
@ConditionalOnClass(RocketMQMessageChannelBinder.class)
public class RocketMQAutoConfiguration {

    @PostConstruct
    public void init() {
        log.info("Shopmind RocketMQ 自动配置已启用");
    }
}
