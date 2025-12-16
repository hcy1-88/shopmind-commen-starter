package com.shopmind.framework.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 异常处理配置属性
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "shopmind.exception-handler")
public class ExceptionHandlerProperties {

    /**
     * 是否启用全局异常处理
     * 默认：true
     */
    private boolean enabled = true;

}
