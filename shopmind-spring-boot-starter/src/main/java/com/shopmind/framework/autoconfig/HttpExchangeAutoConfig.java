package com.shopmind.framework.autoconfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopmind.framework.interceptor.HttpExchangeRequestInterceptor;
import com.shopmind.framework.interceptor.HttpExchangeResponseInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

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
     * 注册 HTTP Exchange 请求拦截器
     * 用于传递 Token 和 TraceId 到下游服务
     */
    @Bean
    public HttpExchangeRequestInterceptor httpExchangeRequestInterceptor() {
        return new HttpExchangeRequestInterceptor(serviceName);
    }

    /**
     * 注册 HTTP Exchange 响应拦截器
     * 用于拦截下游服务的 InternalApiException 并重新抛出
     */
    @Bean
    public HttpExchangeResponseInterceptor httpExchangeResponseInterceptor(ObjectMapper objectMapper) {
        return new HttpExchangeResponseInterceptor(objectMapper);
    }

    /**
     * 通过 @LoadBalanced 注解，RestClient 将能够识别服务名（如 user-service），并通过 Nacos 进行服务发现和负载均衡
     * 同时添加了请求拦截器和响应拦截器
     */
    @Bean
    @LoadBalanced
    public RestClient.Builder restClientBuilder(
            HttpExchangeRequestInterceptor requestInterceptor,
            HttpExchangeResponseInterceptor responseInterceptor) {
        return RestClient.builder()
                .requestInterceptor(requestInterceptor)
                .requestInterceptor(responseInterceptor);
    }
}
