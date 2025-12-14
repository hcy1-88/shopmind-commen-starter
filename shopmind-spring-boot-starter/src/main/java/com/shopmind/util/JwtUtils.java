package com.shopmind.util;

import cn.hutool.core.util.StrUtil;
import com.shopmind.constant.CommonConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

import java.security.PublicKey;
import java.util.Date;

/**
 * JWT 工具类
 */
@Slf4j
public class JwtUtils {

    /**
     * 解析 Token（使用公钥）
     */
    public static Claims parseToken(String token, PublicKey publicKey) {
        try {
            if (StrUtil.isBlank(token) || publicKey == null) {
                return null;
            }

            // 移除 Bearer 前缀
            if (token.startsWith(CommonConstants.TOKEN_PREFIX)) {
                token = token.substring(CommonConstants.TOKEN_PREFIX.length());
            }

            return Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("解析 Token 失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 检查 Token 是否过期
     */
    public static boolean isTokenExpired(Claims claims) {
        if (claims == null) {
            return true;
        }
        Date expiration = claims.getExpiration();
        return expiration != null && expiration.before(new Date());
    }

    /**
     * 从 Claims 中获取字符串值
     */
    public static String getClaimAsString(Claims claims, String key) {
        if (claims == null) {
            return null;
        }
        Object value = claims.get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * 从 Claims 中获取 Long 值
     */
    public static Long getClaimAsLong(Claims claims, String key) {
        if (claims == null) {
            return null;
        }
        Object value = claims.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            log.error("解析 Long 值失败: key={}, value={}", key, value);
            return null;
        }
    }

    /**
     * 从 Claims 中获取 Integer 值
     */
    public static Integer getClaimAsInteger(Claims claims, String key) {
        if (claims == null) {
            return null;
        }
        Object value = claims.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            log.error("解析 Integer 值失败: key={}, value={}", key, value);
            return null;
        }
    }
}
