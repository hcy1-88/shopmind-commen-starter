package com.shopmind.config;

import com.shopmind.interceptor.AuthInterceptor;
import com.shopmind.interceptor.TraceIdInterceptor;
import com.shopmind.properties.AuthProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 配置
 */
@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties(AuthProperties.class)
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthProperties authProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // TraceId 拦截器（优先级最高，所有请求都会经过）
        registry.addInterceptor(new TraceIdInterceptor())
                .addPathPatterns("/**")
                .order(1);

        // 认证拦截器（仅在启用认证时注册）
        if (authProperties.isEnabled()) {
            registry.addInterceptor(new AuthInterceptor(authProperties))
                    .addPathPatterns("/**")
                    .order(2);
        }
    }
}
