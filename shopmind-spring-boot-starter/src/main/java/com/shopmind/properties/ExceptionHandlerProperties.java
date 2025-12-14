package com.shopmind.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 异常处理配置属性
 */
@ConfigurationProperties(prefix = "shopmind.exception-handler")
public class ExceptionHandlerProperties {

    /**
     * 是否启用全局异常处理
     * 默认：true
     */
    private boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
