package com.shopmind.framework.util;

import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * Description: http 客户端工具类
 * Author: huangcy
 * Date: 2025-12-18
 */
public class ShopmindHttpClientUtils {
    /**
     * 创建一个支持客户端负载均衡的 @HttpExchange 接口代理
     *
     * @param loadBalancedBuilder 带有 {@code @LoadBalanced} 的 RestClient.Builder
     * @param serviceName         下游微服务名称（例如 "user-service"）
     * @param clientInterface     使用 {@code @HttpExchange} 注解的接口 Class
     * @param <T>                 客户端接口类型
     * @return 代理实例，可直接注入使用
     */
    public static <T> T createLoadBalancedClient(
            RestClient.Builder loadBalancedBuilder,
            String serviceName,
            Class<T> clientInterface) {

        if (loadBalancedBuilder == null) {
            throw new IllegalArgumentException("RestClient.Builder 不能为空！");
        }
        if (serviceName == null || serviceName.isBlank()) {
            throw new IllegalArgumentException("Service name 不能为空！");
        }
        if (clientInterface == null) {
            throw new IllegalArgumentException("Client interface class 不能为空！");
        }

        // 构建 RestClient，baseUrl = http://服务名
        RestClient restClient = loadBalancedBuilder
                .baseUrl("http://" + serviceName)
                .build();

        // 创建适配器（桥接 RestClient 到 HttpServiceProxyFactory）
        RestClientAdapter adapter = RestClientAdapter.create(restClient);

        // 创建代理工厂
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(adapter)
                .build();

        // 返回动态代理实例
        return factory.createClient(clientInterface);
    }
}
