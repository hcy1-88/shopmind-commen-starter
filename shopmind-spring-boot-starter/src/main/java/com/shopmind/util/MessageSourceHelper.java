package com.shopmind.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * 国际化消息工具类
 * 用于从 Resource Bundle 中读取错误消息
 */
public class MessageSourceHelper {

    private static final Logger log = LoggerFactory.getLogger(MessageSourceHelper.class);

    /**
     * 默认语言环境
     */
    private static final Locale DEFAULT_LOCALE = Locale.SIMPLIFIED_CHINESE;

    /**
     * 从指定的资源文件中获取消息
     *
     * @param baseName 资源文件基础名称（如 "user_message"）
     * @param code     错误码
     * @param args     参数
     * @return 格式化后的消息
     */
    public static String getMessage(String baseName, String code, Object... args) {
        return getMessage(baseName, code, DEFAULT_LOCALE, args);
    }

    /**
     * 从指定的资源文件中获取消息（指定语言环境）
     *
     * @param baseName 资源文件基础名称（如 "user_message"）
     * @param code     错误码
     * @param locale   语言环境
     * @param args     参数
     * @return 格式化后的消息
     */
    public static String getMessage(String baseName, String code, Locale locale, Object... args) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale);
            String message = bundle.getString(code);

            if (args != null && args.length > 0) {
                // 支持 {} 占位符格式（类似 SLF4J）
                return formatMessage(message, args);
            }

            return message;
        } catch (MissingResourceException e) {
            log.warn("未找到错误码 {} 对应的消息，资源文件：{}, 语言环境：{}", code, baseName, locale);
            return code;
        }
    }

    /**
     * 格式化消息
     * 将消息模板中的 {} 替换为实际参数值
     *
     * @param template 消息模板
     * @param args     参数数组
     * @return 格式化后的消息
     */
    private static String formatMessage(String template, Object... args) {
        if (template == null || args == null || args.length == 0) {
            return template;
        }

        String result = template;
        for (Object arg : args) {
            result = result.replaceFirst("\\{\\}", arg != null ? arg.toString() : "null");
        }
        return result;
    }

    /**
     * 检查资源文件中是否存在指定的错误码
     *
     * @param baseName 资源文件基础名称
     * @param code     错误码
     * @return 是否存在
     */
    public static boolean containsKey(String baseName, String code) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(baseName, DEFAULT_LOCALE);
            return bundle.containsKey(code);
        } catch (MissingResourceException e) {
            return false;
        }
    }
}
