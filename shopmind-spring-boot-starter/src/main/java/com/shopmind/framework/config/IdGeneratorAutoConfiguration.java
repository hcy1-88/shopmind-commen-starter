package com.shopmind.framework.config;

import com.shopmind.framework.id.IdGenerator;
import com.shopmind.framework.id.SnowflakeIdGenerator;
import com.shopmind.framework.properties.IdGeneratorProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ID生成器自动配置
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(IdGeneratorProperties.class)
@ConditionalOnProperty(prefix = "shopmind.id-generator", name = "enabled", havingValue = "true", matchIfMissing = true)
public class IdGeneratorAutoConfiguration {

    /**
     * 配置Snowflake ID生成器
     *
     * @param properties ID生成器配置属性
     * @return ID生成器实例
     */
    @Bean
    @ConditionalOnMissingBean
    public IdGenerator idGenerator(IdGeneratorProperties properties) {
        long workerId = getWorkerId(properties);
        long datacenterId = getDatacenterId(properties);

        log.info("初始化 Snowflake ID 生成器 - DatacenterId: {}, WorkerId: {}", datacenterId, workerId);
        return new SnowflakeIdGenerator(workerId, datacenterId);
    }

    /**
     * 获取工作机器ID
     * 优先级：配置文件 > 环境变量 > 系统属性 > 默认值
     *
     * @param properties 配置属性
     * @return 工作机器ID
     */
    private long getWorkerId(IdGeneratorProperties properties) {
        if (!properties.isAutoDetectWorkerId()) {
            return properties.getWorkerId();
        }

        // 1. 优先使用配置文件中的值（如果不是默认值0）
        if (properties.getWorkerId() != 0) {
            return properties.getWorkerId();
        }

        // 2. 尝试从环境变量获取
        String workerIdEnv = System.getenv("SHOPMIND_WORKER_ID");
        if (workerIdEnv != null && !workerIdEnv.isEmpty()) {
            try {
                long workerId = Long.parseLong(workerIdEnv);
                log.info("从环境变量获取 WorkerId: {}", workerId);
                return workerId;
            } catch (NumberFormatException e) {
                log.warn("环境变量 SHOPMIND_WORKER_ID 格式错误: {}", workerIdEnv);
            }
        }

        // 3. 尝试从系统属性获取
        String workerIdProp = System.getProperty("shopmind.workerId");
        if (workerIdProp != null && !workerIdProp.isEmpty()) {
            try {
                long workerId = Long.parseLong(workerIdProp);
                log.info("从系统属性获取 WorkerId: {}", workerId);
                return workerId;
            } catch (NumberFormatException e) {
                log.warn("系统属性 shopmind.workerId 格式错误: {}", workerIdProp);
            }
        }

        // 4. 使用默认值
        log.info("使用默认 WorkerId: {}", properties.getWorkerId());
        return properties.getWorkerId();
    }

    /**
     * 获取数据中心ID
     * 优先级：配置文件 > 环境变量 > 系统属性 > 默认值
     *
     * @param properties 配置属性
     * @return 数据中心ID
     */
    private long getDatacenterId(IdGeneratorProperties properties) {
        if (!properties.isAutoDetectWorkerId()) {
            return properties.getDatacenterId();
        }

        // 1. 优先使用配置文件中的值（如果不是默认值0）
        if (properties.getDatacenterId() != 0) {
            return properties.getDatacenterId();
        }

        // 2. 尝试从环境变量获取
        String datacenterIdEnv = System.getenv("SHOPMIND_DATACENTER_ID");
        if (datacenterIdEnv != null && !datacenterIdEnv.isEmpty()) {
            try {
                long datacenterId = Long.parseLong(datacenterIdEnv);
                log.info("从环境变量获取 DatacenterId: {}", datacenterId);
                return datacenterId;
            } catch (NumberFormatException e) {
                log.warn("环境变量 SHOPMIND_DATACENTER_ID 格式错误: {}", datacenterIdEnv);
            }
        }

        // 3. 尝试从系统属性获取
        String datacenterIdProp = System.getProperty("shopmind.datacenterId");
        if (datacenterIdProp != null && !datacenterIdProp.isEmpty()) {
            try {
                long datacenterId = Long.parseLong(datacenterIdProp);
                log.info("从系统属性获取 DatacenterId: {}", datacenterId);
                return datacenterId;
            } catch (NumberFormatException e) {
                log.warn("系统属性 shopmind.datacenterId 格式错误: {}", datacenterIdProp);
            }
        }

        // 4. 使用默认值
        log.info("使用默认 DatacenterId: {}", properties.getDatacenterId());
        return properties.getDatacenterId();
    }
}
