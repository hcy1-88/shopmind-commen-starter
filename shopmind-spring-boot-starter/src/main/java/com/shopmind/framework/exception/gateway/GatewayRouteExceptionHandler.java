package com.shopmind.framework.exception.gateway;

import com.shopmind.framework.context.ResultContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.resource.NoResourceFoundException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Gateway 路由和负载均衡异常处理
 * 处理 Spring Cloud Gateway 中的路由异常和资源未找到异常
 *
 * 仅在 Spring Cloud Gateway 依赖存在且为 Reactive 应用时生效
 *
 * @author huangcy
 * @date 2025-12-15
 */
@Slf4j
@RestControllerAdvice
public class GatewayRouteExceptionHandler {

    private static final Pattern NO_RESOURCE_PATTERN = Pattern.compile("\"No static resource ([^\"]+)\\.\"");

    /**
     * 处理服务未找到（LoadBalancer 找不到实例）
     * 例如：product-service 未注册到 Nacos
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ResultContext<String>> handleServiceNotFound(NotFoundException e) {
        String message = extractServiceName(e.getMessage());
        String errorMsg = (message != null ? message : "目标服务") + " 服务尚未启动或未注册";
        log.warn("网关路由失败: {}", errorMsg);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ResultContext.fail(errorMsg));
    }

    /**
     * 从异常信息中提取服务名
     * 示例输入: "Unable to find instance for product-service"
     * 返回: "product-service"
     */
    private String extractServiceName(String message) {
        if (message == null) return null;
        // 匹配常见格式
        if (message.contains("Unable to find instance for ")) {
            return message.replace("Unable to find instance for ", "").trim();
        }
        if (message.contains("No instances available for ")) {
            return message.replace("No instances available for ", "").trim();
        }
        return null;
    }

    /**
     * 处理"资源404"异常（例如：如果前端请求了不存在的路由，此时 spring boot 会当做静态资源处理）
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ResultContext<String>> handleNoResourceFound(NoResourceFoundException e) {
        String requestPath = extractRequestPath(e.getMessage());
        String errorMsg = "请求资源 [" + (requestPath != null ? requestPath : "unknown") + "] 不存在！";
        log.warn("网关未找到路径: {}", errorMsg);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResultContext.fail(errorMsg));
    }

    /**
     * 从异常消息中提取请求路径
     * 示例: "No static resource api/product-service/products."
     * 返回: "/api/product-service/products"，如果模式不匹配就会返回 null，但异常message是固定的 不会返回 null
     */
    private String extractRequestPath(String message) {
        if (message == null) {
            return null;
        }

        Matcher matcher = NO_RESOURCE_PATTERN.matcher(message);
        if (matcher.find()) {
            String pathWithoutSlash = matcher.group(1);
            return "/" + pathWithoutSlash;
        }
        return null;
    }
}