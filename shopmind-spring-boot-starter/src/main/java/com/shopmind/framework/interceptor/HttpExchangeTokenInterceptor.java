package com.shopmind.framework.interceptor;

import cn.hutool.core.util.StrUtil;
import com.shopmind.framework.constant.JwtConstants;
import com.shopmind.framework.context.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;

/**
 * HTTP Exchange 请求拦截器
 * <p>
 * 功能：将当前请求的 Token 和 TraceId 传递到下游服务
 * 适用于 Spring 6 HTTP Exchange（基于 RestClient）
 * </p>
 */
@Slf4j
public class HttpExchangeTokenInterceptor implements ClientHttpRequestInterceptor {

    @NotNull
    @Override
    public ClientHttpResponse intercept(@NotNull HttpRequest request, @NotNull byte[] body,
                                        @NotNull ClientHttpRequestExecution execution) throws IOException {
        // 1. 传递 Token
        String token = getToken();
        if (StrUtil.isNotBlank(token)) {
            request.getHeaders().set(JwtConstants.AUTHORIZATION_HEADER, token);
            log.debug("HTTP Exchange 请求携带 Token: {}", request.getURI());
        }

        // 2. 传递 TraceId
        String traceId = UserContext.traceId();
        if (StrUtil.isNotBlank(traceId)) {
            request.getHeaders().set(JwtConstants.TRACE_ID_HEADER, traceId);
            log.debug("HTTP Exchange 请求携带 TraceId: {}, url: {}", traceId, request.getURI());
        }

        // 3. 执行请求
        return execution.execute(request, body);
    }

    /**
     * 获取当前请求的 Token
     * <p>
     * 优先从 HTTP 请求头中获取，如果没有则不传递
     * </p>
     */
    private String getToken() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                return request.getHeader(JwtConstants.AUTHORIZATION_HEADER);
            }
        } catch (Exception e) {
            log.warn("获取 Token 失败: {}", e.getMessage());
        }
        return null;
    }
}