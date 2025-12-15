package com.shopmind.framework.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ID生成器配置属性
 */
@Data
@ConfigurationProperties(prefix = "shopmind.id-generator")
public class IdGeneratorProperties {

    /**
     * 是否启用ID生成器
     */
    private boolean enabled = true;

    /**
     * 数据中心ID (0-31)
     */
    private long datacenterId = 0;

    /**
     * 工作机器ID (0-31)
     */
    private long workerId = 0;

    /**
     * 是否自动从环境变量或系统属性获取workerId
     * 优先级：配置文件 > 环境变量 > 系统属性 > 默认值
     */
    private boolean autoDetectWorkerId = true;
}
