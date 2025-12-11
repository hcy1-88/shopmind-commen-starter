package com.hcy.shopmind.common.util;

import cn.hutool.core.util.IdUtil;

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
}
