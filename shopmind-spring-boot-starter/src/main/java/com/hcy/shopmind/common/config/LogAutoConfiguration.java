package com.hcy.shopmind.common.config;

import com.hcy.shopmind.common.properties.LogProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * Description: log
 * Author: huangcy
 * Date: 2025-12-11
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(LogProperties.class)
public class LogAutoConfiguration {
    @Resource
    private LogProperties properties;

    @PostConstruct
    public void init() {
        log.info("------ log 文件保存在 {}", properties.getPath());
    }
}
