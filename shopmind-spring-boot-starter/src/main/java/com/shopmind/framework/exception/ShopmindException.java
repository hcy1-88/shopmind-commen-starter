package com.shopmind.framework.exception;

import java.io.Serial;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Shopmind 业务异常基类（可被全局处理器捕获 并返回 ResultContext 结果）
 * 所有业务服务的自定义异常都应该继承此类
 */
public class ShopmindException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final String code;

    /**
     * 错误消息（支持动态填充）
     */
    private final String message;

    /**
     * 上下文信息（用于日志/监控）
     */
    private final Map<String, Object> context;

    /**
     * 构造方法1：直接使用错误码
     *
     * @param code 错误码
     */
    public ShopmindException(String code) {
        super(code);
        this.code = code;
        this.message = code;
        this.context = Collections.emptyMap();
    }

    /**
     * 构造方法2：使用错误码和消息
     *
     * @param code    错误码
     * @param message 错误消息
     */
    public ShopmindException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
        this.context = Collections.emptyMap();
    }

    /**
     * 构造方法3：支持动态参数（如 "用户 {} 不是 vip 会员！"）
     * 使用 {} 作为占位符，按顺序替换参数
     *
     * @param code 错误码
     * @param args 动态参数
     */
    public ShopmindException(String code, Object... args) {
        super(formatMessage(code, args));
        this.code = code;
        this.message = formatMessage(code, args);
        this.context = Collections.emptyMap();
    }

    /**
     * 构造方法4：使用错误码、消息和动态参数
     *
     * @param code    错误码
     * @param message 错误消息模板（包含 {} 占位符）
     * @param args    动态参数
     */
    public ShopmindException(String code, String message, Object... args) {
        super(formatMessage(message, args));
        this.code = code;
        this.message = formatMessage(message, args);
        this.context = Collections.emptyMap();
    }

    /**
     * 构造方法5：带上下文（用于日志埋点）
     *
     * @param code    错误码
     * @param message 错误消息
     * @param context 上下文信息
     */
    public ShopmindException(String code, String message, Map<String, Object> context) {
        super(message);
        this.code = code;
        this.message = message;
        this.context = new HashMap<>(context);
    }

    /**
     * 构造方法6：带原始异常
     *
     * @param code    错误码
     * @param message 错误消息
     * @param cause   原始异常
     */
    public ShopmindException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
        this.context = Collections.emptyMap();
    }

    /**
     * 构造方法7：完整参数
     *
     * @param code    错误码
     * @param message 错误消息
     * @param context 上下文信息
     * @param cause   原始异常
     */
    public ShopmindException(String code, String message, Map<String, Object> context, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
        this.context = new HashMap<>(context);
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
            result = result.replaceFirst("\\{}", arg != null ? arg.toString() : "null");
        }
        return result;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Map<String, Object> getContext() {
        return context;
    }

    @Override
    public String toString() {
        return "ShopmindException{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", context=" + context +
                '}';
    }
}
