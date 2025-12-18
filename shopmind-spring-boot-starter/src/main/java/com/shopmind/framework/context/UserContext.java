package com.shopmind.framework.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户上下文信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserContext {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String nickname;

    /**
     * 手机号
     */
    private String phoneNumber;



    /**
     * TraceId
     */
    private String traceId;

    private static final ThreadLocal<UserContext> CONTEXT_HOLDER = new ThreadLocal<>();

    /**
     * 设置用户上下文
     */
    public static void set(UserContext context) {
        CONTEXT_HOLDER.set(context);
    }

    /**
     * 获取用户上下文
     */
    public static UserContext get() {
        return CONTEXT_HOLDER.get();
    }

    /**
     * 清除用户上下文
     */
    public static void clear() {
        CONTEXT_HOLDER.remove();
    }

    /**
     * 获取用户ID
     */
    public static Long userId() {
        UserContext context = get();
        return context != null ? context.getUserId() : null;
    }

    /**
     * 获取用户名
     */
    public static String nickname() {
        UserContext context = get();
        return context != null ? context.getNickname() : null;
    }

    /**
     * 获取手机号
     */
    public static String phoneNumber() {
        UserContext context = get();
        return context != null ? context.getPhoneNumber() : null;
    }


    /**
     * 获取 TraceId
     */
    public static String traceId() {
        UserContext context = get();
        return context != null ? context.getTraceId() : null;
    }
}
