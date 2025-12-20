package com.shopmind.framework.config;

import com.shopmind.framework.interceptor.AuthInterceptor;
import com.shopmind.framework.interceptor.TraceIdInterceptor;
import com.shopmind.framework.properties.AuthProperties;
import com.shopmind.framework.provider.PublicKeyProvider;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 配置（仅在 Servlet/MVC 环境下生效）
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(AuthProperties.class)
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer, ApplicationContextAware {

    private final AuthProperties authProperties;
    private ApplicationContext applicationContext;

    /**
     * 提供 RestTemplate Bean
     */
    @Bean
    @ConditionalOnMissingBean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * 提供 PublicKeyProvider Bean
     */
    @Bean
    @ConditionalOnProperty(name = "shopmind.auth.enabled", havingValue = "true")
    public PublicKeyProvider publicKeyProvider(RestTemplate restTemplate) {
        return new PublicKeyProvider(restTemplate);
    }

    @Bean
    @ConditionalOnProperty(name = "shopmind.auth.enabled", havingValue = "true")
    public AuthInterceptor authInterceptor(
            AuthProperties authProperties,
            PublicKeyProvider publicKeyProvider) {
        return new AuthInterceptor(authProperties, publicKeyProvider);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // TraceId 拦截器（优先级最高，所有请求都会经过）
        registry.addInterceptor(new TraceIdInterceptor())
                .addPathPatterns("/**")
                .order(1);

        // 认证拦截器（仅在启用认证时注册）
        if (authProperties.isEnabled()) {
            registry.addInterceptor(applicationContext.getBean(AuthInterceptor.class))
                    .addPathPatterns("/**")
                    .order(2);
        }
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
