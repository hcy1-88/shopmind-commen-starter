package com.hcy.shopmind.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Description: 日志的属性配置
 * Author: huangcy
 * Date: 2025-12-11
 */
@Data
@ConfigurationProperties(prefix = "shopmind.log")
public class LogProperties {
    private String path;
}
