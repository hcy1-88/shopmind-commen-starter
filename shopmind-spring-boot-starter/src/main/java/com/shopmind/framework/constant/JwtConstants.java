package com.shopmind.framework.constant;

/**
 * 通用常量
 */
public interface JwtConstants {

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
    String JWT_NICKNAME = "nickname";

    /**
     * JWT 中的手机号
     */
    String JWT_PHONE_NUMBER = "phoneNumber";


    /**
     * 加解密算法
     */
    String RSA =  "RSA";
}
