package com.shopmind.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopmind.model.JwksResponse;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.security.KeyFactory;
import java.security.PublicKey;
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
     * 公钥缓存（key: kid, value: PublicKey）
     */
    private final Map<String, PublicKey> publicKeyCache = new ConcurrentHashMap<>();

    /**
     * 默认公钥（当没有指定 kid 时使用）
     */
    @Getter
    private PublicKey defaultPublicKey;

    /**
     * 定时刷新线程池
     */
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    /**
     * 刷新间隔（分钟）
     */
    private static final long REFRESH_INTERVAL_MINUTES = 60;

    public PublicKeyProvider(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;

        // 启动时立即加载公钥
        loadPublicKeys();

        // 定期刷新公钥（每小时）
        scheduler.scheduleAtFixedRate(
                this::loadPublicKeys,
                REFRESH_INTERVAL_MINUTES,
                REFRESH_INTERVAL_MINUTES,
                TimeUnit.MINUTES
        );

        log.info("公钥提供者已启动，认证服务地址: {}", AUTH_SERVICE_URL);
    }

    /**
     * 获取公钥（根据 kid）
     */
    public PublicKey getPublicKey(String kid) {
        if (kid == null || kid.isEmpty()) {
            return defaultPublicKey;
        }

        PublicKey publicKey = publicKeyCache.get(kid);
        if (publicKey == null) {
            log.warn("未找到 kid={} 的公钥，尝试重新加载", kid);
            loadPublicKeys();
            publicKey = publicKeyCache.get(kid);
        }

        return publicKey != null ? publicKey : defaultPublicKey;
    }


    /**
     * 从认证服务加载公钥
     */
    private void loadPublicKeys() {
        try {
            String jwksUrl = AUTH_SERVICE_URL + "/.well-known/jwks.json";
            log.info("正在从认证服务获取公钥: {}", jwksUrl);

            // 方式1：标准 JWKS 格式
            String response = restTemplate.getForObject(jwksUrl, String.class);
            if (response == null) {
                log.error("从认证服务获取公钥失败: 响应为空");
                return;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JwksResponse jwksResponse = objectMapper.readValue(response, JwksResponse.class);

            if (jwksResponse.getKeys() == null || jwksResponse.getKeys().isEmpty()) {
                log.error("从认证服务获取公钥失败: keys 为空");
                return;
            }

            // 清空旧缓存
            publicKeyCache.clear();

            // 加载所有公钥
            for (JwksResponse.JwkKey jwk : jwksResponse.getKeys()) {
                try {
                    PublicKey publicKey = parsePublicKey(jwk);
                    if (publicKey != null) {
                        String kid = jwk.getKid() != null ? jwk.getKid() : "default";
                        publicKeyCache.put(kid, publicKey);

                        // 第一个密钥作为默认密钥
                        if (defaultPublicKey == null) {
                            defaultPublicKey = publicKey;
                        }

                        log.info("成功加载公钥: kid={}, alg={}", kid, jwk.getAlg());
                    }
                } catch (Exception e) {
                    log.error("解析公钥失败: kid={}", jwk.getKid(), e);
                }
            }

            log.info("公钥加载完成，共 {} 个", publicKeyCache.size());

        } catch (Exception e) {
            log.error("从认证服务加载公钥失败", e);
        }
    }

    /**
     * 解析公钥
     */
    private PublicKey parsePublicKey(JwksResponse.JwkKey jwk) throws Exception {
        // 如果提供了 PEM 格式公钥（简化方案）
        if (jwk.getPublicKey() != null && !jwk.getPublicKey().isEmpty()) {
            return parsePublicKeyFromPem(jwk.getPublicKey());
        }

        // 标准 JWKS 格式（RSA）
        if ("RSA".equals(jwk.getKty()) && jwk.getN() != null && jwk.getE() != null) {
            // TODO: 实现标准 JWKS 格式解析（需要额外依赖）
            log.warn("暂不支持标准 JWKS 格式，请使用 PEM 格式公钥");
            return null;
        }

        return null;
    }

    /**
     * 从 PEM 格式解析公钥
     */
    private PublicKey parsePublicKeyFromPem(String pemPublicKey) throws Exception {
        // 移除 PEM 头尾和换行符
        String publicKeyPEM = pemPublicKey
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        // Base64 解码
        byte[] decoded = Base64.getDecoder().decode(publicKeyPEM);

        // 生成公钥
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

    /**
     * 关闭
     */
    public void shutdown() {
        scheduler.shutdown();
        log.info("公钥提供者已关闭");
    }
}
