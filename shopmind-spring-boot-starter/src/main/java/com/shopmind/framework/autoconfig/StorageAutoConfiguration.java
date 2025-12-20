package com.shopmind.framework.autoconfig;

import com.shopmind.framework.id.IdGenerator;
import com.shopmind.framework.properties.StorageProperties;
import com.shopmind.framework.service.StorageService;
import com.shopmind.framework.service.impl.RustFSStorageImpl;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

/**
 * Description: RustFS自动配置类
 * Author: huangcy
 * Date: 2025-12-16
 */
@Slf4j
@Validated
@Configuration
@EnableConfigurationProperties(StorageProperties.class)
@ConditionalOnClass(S3Client.class)
@ConditionalOnProperty(prefix = "shopmind.storage", name = "enabled", havingValue = "true")
public class StorageAutoConfiguration {

    @Resource
    private IdGenerator idGenerator;

    @Bean
    @ConditionalOnMissingBean(S3Client.class)
    @ConditionalOnProperty(prefix = "shopmind.storage", name = "provider", havingValue = "rustfs")
    public S3Client s3Client(StorageProperties properties) { // ← 通过方法参数注入已初始化的 properties
        return S3Client.builder()
                .endpointOverride(URI.create(properties.getRustfs().getEndpoint()))
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(properties.getRustfs().getAccessKey(), properties.getRustfs().getSecretKey())))
                .forcePathStyle(true)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(StorageService.class)
    @ConditionalOnProperty(prefix = "shopmind.storage", name = "provider", havingValue = "rustfs")
    public StorageService storageService(StorageProperties properties, S3Client s3Client) {
        return new RustFSStorageImpl(s3Client, properties, idGenerator);
    }

    @PostConstruct
    public void init() {
        log.info("Shopmind RustFS 自动配置已启用");
    }
}
