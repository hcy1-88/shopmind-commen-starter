package com.shopmind.framework.util;

import cn.hutool.core.util.IdUtil;
import com.shopmind.framework.constant.JwtConstants;
import org.slf4j.MDC;

/**
 * TraceId 工具类
 */
public class TraceIdUtils {

    /**
     * 生成 TraceId
     */
    public static String generateTraceId() {
        return IdUtil.fastSimpleUUID();
    }

    /**
     * 获取当前请求的 TraceId
     * 从 MDC 中获取，与日志系统保持一致
     *
     * @return TraceId
     */
    public static String getCurrentTraceId() {
        String traceId = MDC.get(JwtConstants.TRACE_ID_HEADER);
        return traceId != null ? traceId : "unknown";
    }
}
