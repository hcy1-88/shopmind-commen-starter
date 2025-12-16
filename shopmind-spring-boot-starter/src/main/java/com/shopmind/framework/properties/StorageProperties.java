package com.shopmind.framework.properties;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Description: RustFS配置
 * Author: huangcy
 * Date: 2025-12-16
 */
@Data
@Validated
@ConfigurationProperties(prefix = "shopmind.storage")
public class StorageProperties {

    private boolean enabled = false;

    @NotBlank
    private String provider = "rustfs";

    @NotNull
    private RustFS rustfs = new RustFS();

    @Data
    public static class RustFS {
        @NotBlank
        private String endpoint;

        @NotBlank
        private String bucketName = "default-bucket";

        @NotBlank
        private String accessKey = "rustfsadmin";

        @NotBlank
        private String secretKey = "rustfsadmin";

        @Min(1)
        @Max(100)
        private Integer chunkSize = 5;
    }
}
