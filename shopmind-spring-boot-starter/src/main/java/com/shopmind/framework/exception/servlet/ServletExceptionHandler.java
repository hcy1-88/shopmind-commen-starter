package com.shopmind.framework.exception.servlet;

import com.shopmind.framework.context.ResultContext;
import com.shopmind.framework.exception.ShopmindException;
import com.shopmind.framework.util.TraceIdUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
                TraceIdUtils.getCurrentTraceId(),
                e);

        return ResultContext.<Void>failBuilder()
                .code(e.getCode())
                .message(e.getMessage())
                .build();
    }


}
