package com.shopmind.constant;

/**
 * 通用常量
 */
public interface CommonConstants {

    /**
     * Token 请求头名称
     */
    String AUTHORIZATION_HEADER = "Authorization";

    /**
     * TraceId 请求头名称
     */
    String TRACE_ID_HEADER = "X-Trace-ID";

    /**
     * Token 前缀
     */
    String TOKEN_PREFIX = "Bearer ";

    /**
     * JWT 中的用户ID
     */
    String JWT_USER_ID = "userId";

    /**
     * JWT 中的用户名
     */
    String JWT_USERNAME = "username";

    /**
     * JWT 中的手机号
     */
    String JWT_PHONE = "phone";

    /**
     * JWT 中的邮箱
     */
    String JWT_EMAIL = "email";

    /**
     * JWT 中的用户类型
     */
    String JWT_USER_TYPE = "userType";
}
