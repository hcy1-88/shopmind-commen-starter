package com.shopmind.model;

import lombok.Data;
import lombok.Getter;

import java.util.List;

/**
 * JWKS (JSON Web Key Set) 响应
 * 符合 RFC 7517 标准
 */
@Data
public class JwksResponse {

    /**
     * 公钥列表
     */
    private List<JwkKey> keys;


    /**
     * JSON Web Key
     */
    @Data
    public static class JwkKey {
        /**
         * 密钥类型（RSA / EC）
         */
        private String kty;

        /**
         * 密钥用途（sig / enc）
         */
        private String use;

        /**
         * 密钥 ID（用于密钥轮换）
         */
        private String kid;

        /**
         * 算法（RS256 / ES256）
         */
        private String alg;

        /**
         * RSA 公钥模数（Base64 URL 编码）
         */
        private String n;

        /**
         * RSA 公钥指数（Base64 URL 编码）
         */
        private String e;

        /**
         * PEM 格式公钥（扩展字段，便于使用）
         */
        private String publicKey;


    }
}
