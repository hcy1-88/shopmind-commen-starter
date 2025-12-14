package com.shopmind.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopmind.constant.CommonConstants;
import com.shopmind.model.JwksResponse;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 公钥提供者
 * 从认证服务获取公钥并缓存
 */
public class PublicKeyProvider {

    private static final Logger log = LoggerFactory.getLogger(PublicKeyProvider.class);

    /**
     * 认证服务地址（硬编码，通过网关访问）
     */
    private static final String AUTH_SERVICE_URL = "http://auth-service";

    /**
     * REST 客户端
     */
    private final RestTemplate restTemplate;


    /**
     * 默认公钥（当没有指定 kid 时使用）
     */
    private PublicKey publicKey;


    public PublicKeyProvider(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }



    /**
     * 从认证服务加载公钥
     */
    public PublicKey getPublicKey() {
        try {
            if (this.publicKey != null) {
                return this.publicKey;
            }
            // 1. 从 auth-service 获取 jwk json
            String jwksUrl = AUTH_SERVICE_URL + "/.well-known/jwks.json";
            log.info("正在从认证服务获取公钥: {}", jwksUrl);
            String jwksJson = restTemplate.getForObject(jwksUrl, String.class);
            if (jwksJson == null) {
                log.error("从认证服务获取公钥失败: 响应为空");
                return null;
            }

            // 2. 解析 JSON，提取 n 和 e（Base64Url 编码）
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jwksJson);
            JsonNode key = root.get("keys").get(0);

            String nBase64Url = key.get("n").asText(); // modulus
            String eBase64Url = key.get("e").asText(); // exponent

            // 3. 转为 BigInteger（需补 '=' 填充，并转标准 Base64）
            byte[] nBytes = Base64.getUrlDecoder().decode(nBase64Url);
            byte[] eBytes = Base64.getUrlDecoder().decode(eBase64Url);

            BigInteger modulus = new BigInteger(1, nBytes);
            BigInteger exponent = new BigInteger(1, eBytes);

            // 4. 重建 PublicKey
            RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
            PublicKey publicKey = KeyFactory.getInstance(CommonConstants.RSA).generatePublic(spec);
            this.publicKey = publicKey;
            log.info("------------------ 公钥加载完成！");
            return publicKey;
        } catch (Exception e) {
            log.error("从认证服务加载公钥失败", e);
        }
        return null;
    }

}
