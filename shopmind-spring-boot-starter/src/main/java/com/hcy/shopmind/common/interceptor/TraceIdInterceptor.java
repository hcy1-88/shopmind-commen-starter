package com.hcy.shopmind.common.interceptor;

import cn.hutool.core.util.StrUtil;
import com.hcy.shopmind.common.constant.CommonConstants;
import com.hcy.shopmind.common.context.UserContext;
import com.hcy.shopmind.common.util.TraceIdUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * TraceId 拦截器
 * 负责生成或传递 TraceId，用于链路追踪
 */
@Slf4j
public class TraceIdInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 从请求头获取 TraceId，如果没有则生成一个新的
        String traceId = request.getHeader(CommonConstants.TRACE_ID_HEADER);
        if (StrUtil.isBlank(traceId)) {
            traceId = TraceIdUtils.generateTraceId();
        }

        // 将 TraceId 放入 MDC（用于日志打印）
        MDC.put(CommonConstants.TRACE_ID_HEADER, traceId);

        // 将 TraceId 放入响应头（方便前端追踪）
        response.setHeader(CommonConstants.TRACE_ID_HEADER, traceId);

        // 将 TraceId 放入 UserContext
        UserContext context = UserContext.get();
        if (context == null) {
            context = UserContext.builder().traceId(traceId).build();
            UserContext.set(context);
        } else {
            context.setTraceId(traceId);
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清理 MDC 和 UserContext
        MDC.remove(CommonConstants.TRACE_ID_HEADER);
        UserContext.clear();
    }
}
