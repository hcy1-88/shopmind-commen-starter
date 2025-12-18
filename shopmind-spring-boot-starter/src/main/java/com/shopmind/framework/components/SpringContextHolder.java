package com.shopmind.framework.components;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring 上下文工具类，用于在非 Spring 管理的类（如静态方法）中获取 Bean
 */
@Component
public class SpringContextHolder implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        applicationContext = ctx;
    }

    /**
     * 获取指定类型的 Bean（要求容器中只有一个该类型的 Bean）
     */
    public static <T> T getBean(Class<T> clazz) {
        if (applicationContext == null) {
            throw new IllegalStateException("SpringContextHolder 尚未初始化，无法获取 Bean");
        }
        return applicationContext.getBean(clazz);
    }

    /**
     * 根据名称和类型获取 Bean
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        if (applicationContext == null) {
            throw new IllegalStateException("SpringContextHolder 尚未初始化，无法获取 Bean");
        }
        return applicationContext.getBean(name, clazz);
    }

    /**
     * 检查是否包含某个 Bean
     */
    public static boolean containsBean(String name) {
        return applicationContext != null && applicationContext.containsBean(name);
    }
}