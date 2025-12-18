package com.shopmind.framework.util;

import cn.hutool.core.util.StrUtil;
import com.shopmind.framework.constant.JwtConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

/**
 * JWT 工具类
 * 注意：此工具类只提供 Token 验证功能，不提供生成功能
 * Token 生成应该只在 auth-service 中进行，保证安全性
 */
@Slf4j
public class JwtUtils {

    /**
     * 工具：从 Base64 字符串加载 PublicKey
     */
    public static PublicKey loadPublicKeyFromBase64(String base64) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    /**
     * 工具：从 Base64 字符串加载 PrivateKey
     */
    public static PrivateKey loadPrivateKeyFromBase64(String base64) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    /**
     * 解析 Token（使用公钥）
     */
    public static Claims parseToken(String token, PublicKey publicKey) {
        if (StrUtil.isBlank(token) || publicKey == null) {
            return null;
        }

        // 移除 Bearer 前缀
        if (token.startsWith(JwtConstants.TOKEN_PREFIX)) {
            token = token.substring(JwtConstants.TOKEN_PREFIX.length());
        }

        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
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
