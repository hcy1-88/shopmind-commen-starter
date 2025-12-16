package com.shopmind.framework.autoconfig;

import com.shopmind.framework.interceptor.FeignTokenInterceptor;
import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Feign 自动配置
 * <p>
 * 功能：配置 Feign 请求拦截器，自动传递 Token 和 TraceId
 * </p>
 */
@Slf4j
@Configuration
@ConditionalOnClass(name = "feign.RequestInterceptor")
public class FeignAutoConfiguration {

    @Bean
    public RequestInterceptor feignTokenInterceptor() {
        log.info("Feign Token 拦截器已启用");
        return new FeignTokenInterceptor();
    }
}