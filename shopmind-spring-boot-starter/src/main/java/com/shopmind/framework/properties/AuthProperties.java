package com.shopmind.framework.properties;

import com.shopmind.framework.constant.ServiceNameConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 认证配置属性
 * <p>
 * 设计说明：
 * 1. 默认所有接口都是公开的，业务接口通过 @RequireAuth 注解标记需要认证
 * 2. systemWhitelist 仅用于系统级接口（如监控、文档），一般不需要修改
 * </p>
 */
@Data
@ConfigurationProperties(prefix = "shopmind.auth")
public class AuthProperties {

    /**
     * 是否启用认证拦截器
     */
    private boolean enabled = false;

    /**
     * 系统白名单路径（用于系统级接口，一般无需修改）
     * 包含：健康检查、监控端点、API 文档、静态资源等
     */
    private List<String> systemWhitelist = new ArrayList<>(List.of(
            "/error",
            "/actuator/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/webjars/**",
            "/favicon.ico"
    ));

    /**
     * 是否打印认证日志
     */
    private boolean logEnabled = true;

    /**
     * 获取公钥的 url
     */
    private String publicKeyUrl = "http://" + ServiceNameConstant.AUTH_SERVICE + "/.well-known/jwks.json";
}
