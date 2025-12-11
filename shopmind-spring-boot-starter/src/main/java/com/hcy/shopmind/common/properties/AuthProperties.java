package com.hcy.shopmind.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 认证配置属性
 */
@Data
@ConfigurationProperties(prefix = "shopmind.auth")
public class AuthProperties {

    /**
     * 是否启用认证
     */
    private boolean enabled = true;

    /**
     * JWT 密钥
     */
    private String jwtSecret = "shopmind-default-secret-key-please-change-it-in-production";

    /**
     * 白名单路径（不需要登录的接口）
     * 默认包含常见的健康检查、文档、静态资源等路径
     */
    private List<String> whitelist = new ArrayList<>(List.of(
            "/error",
            "/actuator/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/webjars/**",
            "/favicon.ico",
            "/health",
            "/info"
    ));

    /**
     * 是否打印认证日志
     */
    private boolean logEnabled = true;
}
