package com.shopmind.framework.context;

import com.shopmind.framework.util.TraceIdUtils;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 统一接口返回类型
 *
 * @param <T> 返回数据类型
 */
@Data
public class ResultContext<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 成功状态码
     */
    public static final String SUCCESS_CODE = "0";

    /**
     * 系统异常状态码
     */
    public static final String SYSTEM_ERROR_CODE = "SYS9999";

    /**
     * 返回数据
     */
    private T data;

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 状态码
     */
    private String code;

    /**
     * 消息
     */
    private String message;

    /**
     * 链路追踪ID
     */
    private String traceId;

    /**
     * 额外信息，用于向后兼容
     */
    private Map<String, Object> extra;

    public ResultContext() {
        this.extra = new HashMap<>();
        this.traceId = TraceIdUtils.getCurrentTraceId();
    }

    public ResultContext(boolean success, String code, String message, T data) {
        this();
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // ==================== 静态工厂方法 ====================

    /**
     * 成功返回（无数据）
     */
    public static <T> ResultContext<T> success() {
        return new ResultContext<>(true, SUCCESS_CODE, "操作成功", null);
    }

    /**
     * 成功返回（带数据）
     */
    public static <T> ResultContext<T> success(T data) {
        return new ResultContext<>(true, SUCCESS_CODE, "操作成功", data);
    }

    /**
     * 成功返回（带数据和消息）
     */
    public static <T> ResultContext<T> success(T data, String message) {
        return new ResultContext<>(true, SUCCESS_CODE, message, data);
    }

    /**
     * 失败返回（默认消息）
     */
    public static <T> ResultContext<T> fail() {
        return new ResultContext<>(false, SYSTEM_ERROR_CODE, "操作失败", null);
    }

    /**
     * 失败返回（带消息）
     */
    public static <T> ResultContext<T> fail(String message) {
        return new ResultContext<>(false, SYSTEM_ERROR_CODE, message, null);
    }

    /**
     * 失败返回（带状态码和消息）
     */
    public static <T> ResultContext<T> fail(String code, String message) {
        return new ResultContext<>(false, code, message, null);
    }

    /**
     * 失败返回（带状态码、消息和数据）
     */
    public static <T> ResultContext<T> fail(String code, String message, T data) {
        return new ResultContext<>(false, code, message, data);
    }

    // ==================== Builder 构建者模式 ====================

    /**
     * 开始构建成功响应
     */
    public static <T> Builder<T> successBuilder() {
        return new Builder<T>().success(true).code(SUCCESS_CODE).message("操作成功");
    }

    /**
     * 开始构建失败响应
     */
    public static <T> Builder<T> failBuilder() {
        return new Builder<T>().success(false).code(SYSTEM_ERROR_CODE).message("操作失败");
    }

    /**
     * 自定义构建
     */
    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {
        private T data;
        private boolean success;
        private String code;
        private String message;
        private String traceId;
        private Map<String, Object> extra = new HashMap<>();

        public Builder<T> data(T data) {
            this.data = data;
            return this;
        }

        public Builder<T> success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder<T> code(String code) {
            this.code = code;
            return this;
        }

        public Builder<T> message(String message) {
            this.message = message;
            return this;
        }

        public Builder<T> traceId(String traceId) {
            this.traceId = traceId;
            return this;
        }

        public Builder<T> extra(Map<String, Object> extra) {
            this.extra = extra;
            return this;
        }

        public Builder<T> putExtra(String key, Object value) {
            this.extra.put(key, value);
            return this;
        }

        public ResultContext<T> build() {
            ResultContext<T> result = new ResultContext<>();
            result.data = this.data;
            result.success = this.success;
            result.code = this.code;
            result.message = this.message;
            result.traceId = this.traceId != null ? this.traceId : TraceIdUtils.getCurrentTraceId();
            result.extra = this.extra;
            return result;
        }
    }

}
