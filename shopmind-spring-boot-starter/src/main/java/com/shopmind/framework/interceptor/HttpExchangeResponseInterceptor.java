package com.shopmind.framework.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopmind.framework.context.ResultContext;
import com.shopmind.framework.exception.InternalApiException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * HTTP Exchange 响应拦截器
 * <p>
 * 功能：拦截下游服务返回的 ResultContext，如果 success=false，抛出异常到上游
 * 适用于 Spring 6 HTTP Exchange（基于 RestClient）
 * </p>
 */
@Slf4j
public class HttpExchangeResponseInterceptor implements ClientHttpRequestInterceptor {

    private final ObjectMapper objectMapper;

    public HttpExchangeResponseInterceptor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @NotNull
    @Override
    public ClientHttpResponse intercept(@NotNull HttpRequest request, @NotNull byte[] body,
                                        @NotNull ClientHttpRequestExecution execution) throws IOException {
        // 1. 执行请求，获取响应
        ClientHttpResponse response = execution.execute(request, body);

        // 2. 读取响应体
        byte[] responseBody = response.getBody().readAllBytes();
        String responseStr = new String(responseBody, StandardCharsets.UTF_8);

        // 3. 尝试解析为 ResultContext
        try {
            ResultContext<?> resultContext = objectMapper.readValue(responseStr, ResultContext.class);

            // 4. 如果 success=false，抛出业务异常
            if (!resultContext.isSuccess()) {
                log.warn("下游服务返回业务失败: code={}, message={}, url={}",
                        resultContext.getCode(), resultContext.getMessage(), request.getURI());

                throw new InternalApiException(resultContext.getCode(), resultContext.getMessage());
            }

            // 5. success=true，正常返回（需要重新包装响应，因为 body 已被读取）
            log.debug("下游服务返回业务成功: url={}", request.getURI());
            return new BufferingClientHttpResponseWrapper(response, responseBody);

        } catch (InternalApiException e) {
            // 重新抛出业务异常
            throw e;
        } catch (Exception e) {
            // 解析失败（说明不是 ResultContext 格式），正常返回原始响应
            log.debug("响应不是 ResultContext 格式，正常返回: url={}", request.getURI());
            return new BufferingClientHttpResponseWrapper(response, responseBody);
        }
    }

    /**
     * 可重复读取的响应包装类
     * 因为原始响应的 InputStream 只能读取一次，需要将内容缓存到字节数组中
     */
    private static class BufferingClientHttpResponseWrapper implements ClientHttpResponse {
        private final ClientHttpResponse response;
        private final byte[] body;

        public BufferingClientHttpResponseWrapper(ClientHttpResponse response, byte[] body) {
            this.response = response;
            this.body = body;
        }

        @NotNull
        @Override
        public HttpStatusCode getStatusCode() throws IOException {
            return response.getStatusCode();
        }

        @NotNull
        @Override
        public String getStatusText() throws IOException {
            return response.getStatusText();
        }

        @Override
        public void close() {
            response.close();
        }

        @NotNull
        @Override
        public InputStream getBody() throws IOException {
            return new ByteArrayInputStream(body);
        }

        @NotNull
        @Override
        public HttpHeaders getHeaders() {
            return response.getHeaders();
        }
    }
}