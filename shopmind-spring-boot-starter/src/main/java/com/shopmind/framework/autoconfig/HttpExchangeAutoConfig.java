package com.shopmind.framework.autoconfig;

import com.shopmind.framework.interceptor.HttpExchangeTokenInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * Description: http exchange 自动配置类
 * Author: huangcy
 * Date: 2025-12-18
 */
@Configuration
public class HttpExchangeAutoConfig {

    @Value("${spring.application.name}")
    private String serviceName;

    /**
     * 注册 HTTP Exchange 拦截器
     * 用于传递 Token 和 TraceId 到下游服务
     */
    @Bean
    public HttpExchangeTokenInterceptor httpExchangeTokenInterceptor() {
        return new HttpExchangeTokenInterceptor(serviceName);
    }

    /**
     * 通过 @LoadBalanced 注解，RestClient 将能够识别服务名（如 user-service），并通过 Nacos 进行服务发现和负载均衡
     * 同时添加了 HttpExchangeTokenInterceptor 拦截器，用于传递 Token 和 TraceId
     */
    @Bean
    @LoadBalanced
    public RestClient.Builder restClientBuilder(HttpExchangeTokenInterceptor interceptor) {
        return RestClient.builder()
                .requestInterceptor(interceptor);
    }
}
