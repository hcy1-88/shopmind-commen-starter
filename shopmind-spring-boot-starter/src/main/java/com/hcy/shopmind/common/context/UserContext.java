package com.hcy.shopmind.common.context;

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
    private String username;

    /**
     * 手机号
     */
    private String phone;


    /**
     * 用户类型（1:普通用户 2:商家 3:管理员）
     */
    private Integer userType;

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
    public static String username() {
        UserContext context = get();
        return context != null ? context.getUsername() : null;
    }

    /**
     * 获取手机号
     */
    public static String phone() {
        UserContext context = get();
        return context != null ? context.getPhone() : null;
    }


    /**
     * 获取用户类型
     */
    public static Integer userType() {
        UserContext context = get();
        return context != null ? context.getUserType() : null;
    }

    /**
     * 获取 TraceId
     */
    public static String traceId() {
        UserContext context = get();
        return context != null ? context.getTraceId() : null;
    }
}
