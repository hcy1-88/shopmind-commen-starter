package com.shopmind.interceptor;

import cn.hutool.core.util.StrUtil;
import com.shopmind.constant.CommonConstants;
import com.shopmind.context.UserContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Feign 请求拦截器
 * <p>
 * 功能：将当前请求的 Token 和 TraceId 传递到下游服务
 * </p>
 */
@Slf4j
public class FeignTokenInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        // 1. 传递 Token
        String token = getToken();
        if (StrUtil.isNotBlank(token)) {
            template.header(CommonConstants.AUTHORIZATION_HEADER, token);
            log.debug("Feign 请求携带 Token: {}", template.url());
        }

        // 2. 传递 TraceId
        String traceId = UserContext.traceId();
        if (StrUtil.isNotBlank(traceId)) {
            template.header(CommonConstants.TRACE_ID_HEADER, traceId);
            log.debug("Feign 请求携带 TraceId: {}, url: {}", traceId, template.url());
        }
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
                return request.getHeader(CommonConstants.AUTHORIZATION_HEADER);
            }
        } catch (Exception e) {
            log.warn("获取 Token 失败: {}", e.getMessage());
        }
        return null;
    }
}