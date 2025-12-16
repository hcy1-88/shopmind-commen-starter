package com.shopmind.framework.exception.servlet;

import com.shopmind.framework.context.ResultContext;
import com.shopmind.framework.constant.CommonConstants;
import com.shopmind.framework.exception.ShopmindException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 全局异常处理切面
 * 统一处理业务异常和系统异常
 * <p>
 * 可通过配置 shopmind.exception-handler.enabled=false 禁用
 */
@RestControllerAdvice
public class ServletExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ServletExceptionHandler.class);

    /**
     * 处理业务异常
     * 业务开发人员主动抛出的异常，如 UserServiceException
     *
     * @param e ShopmindException
     * @return ResultContext
     */
    @ExceptionHandler(ShopmindException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResultContext<Void> handleShopmindException(ShopmindException e) {
        // 业务异常通常是预期内的，使用 warn 级别
        log.warn("业务异常: code={}, message={}, context={}, traceId={}",
                e.getCode(),
                e.getMessage(),
                e.getContext(),
                getCurrentTraceId(),
                e);

        return ResultContext.<Void>failBuilder()
                .code(e.getCode())
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResultContext<Void> handleNoResourceFoundException(NoResourceFoundException e) {
        log.warn("资源路径不存在：message={}, traceId:{}", e.getMessage(), getCurrentTraceId());
        // 正确的正则：注意只有两个反斜杠
        Pattern pattern = Pattern.compile("No static resource ([^.]+)\\.");
        Matcher matcher = pattern.matcher(e.getMessage());
        String path = e.getMessage();
        if (matcher.find()) {
            path = matcher.group(1);
        }
        return ResultContext.fail(e.getStatusCode() + "", "请求路径不存在：" + path);
    }


    /**
     * 处理参数校验异常
     * Spring Validation 注解校验失败时抛出
     *
     * @param e MethodArgumentNotValidException
     * @return ResultContext
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultContext<Void> handleValidationException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        log.warn("参数校验异常: message={}, traceId={}",
                errorMessage,
                getCurrentTraceId(),
                e);

        return ResultContext.<Void>failBuilder()
                .code("PARAM_INVALID")
                .message("参数校验失败: " + errorMessage)
                .build();
    }

    /**
     * 处理非法参数异常
     *
     * @param e IllegalArgumentException
     * @return ResultContext
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultContext<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("非法参数异常: message={}, traceId={}",
                e.getMessage(),
                getCurrentTraceId(),
                e);

        return ResultContext.<Void>failBuilder()
                .code("PARAM_INVALID")
                .message(e.getMessage() != null ? e.getMessage() : "参数非法")
                .build();
    }

    /**
     * 处理空指针异常
     *
     * @param e NullPointerException
     * @return ResultContext
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultContext<Void> handleNullPointerException(NullPointerException e) {
        log.error("空指针异常: traceId={}",
                getCurrentTraceId(),
                e);

        return ResultContext.<Void>failBuilder()
                .code(ResultContext.SYSTEM_ERROR_CODE)
                .message("系统内部错误")
                .build();
    }

    /**
     * 处理系统异常
     * 未被其他 Handler 捕获的所有异常
     *
     * @param e Exception
     * @return ResultContext
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultContext<Void> handleException(Exception e) {
        // 系统异常通常是非预期的，使用 error 级别
        log.error("系统异常: exceptionType={}, message={}, traceId={}",
                e.getClass().getName(),
                e.getMessage(),
                getCurrentTraceId(),
                e);

        return ResultContext.<Void>failBuilder()
                .code(ResultContext.SYSTEM_ERROR_CODE)
                .message("系统内部错误，请稍后重试")
                .build();
    }

    /**
     * 获取当前请求的 TraceId
     * 从 MDC 中获取，与日志系统保持一致
     *
     * @return TraceId
     */
    private String getCurrentTraceId() {
        String traceId = MDC.get(CommonConstants.TRACE_ID_HEADER);
        return traceId != null ? traceId : "unknown";
    }
}
