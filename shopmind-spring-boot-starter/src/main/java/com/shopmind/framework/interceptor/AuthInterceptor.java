package com.shopmind.framework.interceptor;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.shopmind.framework.annotation.RequireAuth;
import com.shopmind.framework.constant.JwtConstants;
import com.shopmind.framework.context.UserContext;
import com.shopmind.framework.properties.AuthProperties;
import com.shopmind.framework.provider.PublicKeyProvider;
import com.shopmind.framework.util.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;
import java.security.PublicKey;
import java.util.List;

/**
 * 认证拦截器（注解驱动）
 * <p>
 * 设计理念：
 * 1. 默认所有接口都是公开的（适合电商等 C 端项目）
 * 2. 只有标记了 @RequireAuth 的接口才需要认证
 * 3. 系统接口（如 /actuator/**）通过配置白名单自动放行
 * </p>
 */
@Slf4j
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final AuthProperties authProperties;
    private final PublicKeyProvider publicKeyProvider;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestUri = request.getRequestURI();

        // 1. 检查系统白名单（如 /actuator/**、/swagger-ui/** 等）
        if (isInSystemWhitelist(requestUri)) {
            if (authProperties.isLogEnabled()) {
                log.debug("系统白名单路径，跳过认证: {}", requestUri);
            }
            return true;
        }

        // 2. 检查是否为 HandlerMethod（Controller 方法）
        if (!(handler instanceof HandlerMethod)) {
            // 不是 Controller 方法（如静态资源），直接放行
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        Class<?> clazz = handlerMethod.getBeanType();

        // 3. 检查方法或类上是否有 @RequireAuth 注解
        boolean requireAuth = method.isAnnotationPresent(RequireAuth.class)
                || clazz.isAnnotationPresent(RequireAuth.class);

        // 4. 如果不需要认证，直接放行
        if (!requireAuth) {
            if (authProperties.isLogEnabled()) {
                log.debug("公开接口，无需认证: {}", requestUri);
            }
            return true;
        }

        // 5. 需要认证，开始验证 Token
        if (authProperties.isLogEnabled()) {
            log.debug("需要认证的接口，开始验证 Token: {}", requestUri);
        }

        // 获取 Token
        String token = request.getHeader(JwtConstants.AUTHORIZATION_HEADER);
        if (StrUtil.isBlank(token)) {
            log.warn("请求未携带 Token: {}", requestUri);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        // 解析 Token（使用公钥）
        PublicKey publicKey = publicKeyProvider.getPublicKey();
        if (publicKey == null) {
            log.error("无法获取公钥，请检查认证服务是否正常");
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return false;
        }

        Claims claims = JwtUtils.parseToken(token, publicKey);
        if (claims == null) {
            log.warn("Token 解析失败: {}", requestUri);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        // 检查 Token 是否过期
        if (JwtUtils.isTokenExpired(claims)) {
            log.warn("Token 已过期: {}", requestUri);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        // 提取用户信息并注入到 UserContext
        Long userId = JwtUtils.getClaimAsLong(claims, JwtConstants.JWT_USER_ID);
        String username = JwtUtils.getClaimAsString(claims, JwtConstants.JWT_USERNAME);
        String phone = JwtUtils.getClaimAsString(claims, JwtConstants.JWT_PHONE);
        Integer userType = JwtUtils.getClaimAsInteger(claims, JwtConstants.JWT_USER_TYPE);

        // 获取或创建 UserContext（可能已经由 TraceIdInterceptor 创建）
        UserContext context = UserContext.get();
        if (context == null) {
            context = UserContext.builder().build();
        }

        context.setUserId(userId);
        context.setUsername(username);
        context.setPhone(phone);
        context.setUserType(userType);

        UserContext.set(context);

        if (authProperties.isLogEnabled()) {
            log.debug("用户认证成功: userId={}, username={}, uri={}", userId, username, requestUri);
        }

        return true;
    }

    /**
     * 检查请求路径是否在系统白名单中
     * 系统白名单用于放行系统级别的接口（如健康检查、监控、文档等）
     */
    private boolean isInSystemWhitelist(String requestUri) {
        List<String> whiteList = authProperties.getSystemWhitelist();
        if (CollectionUtil.isEmpty(whiteList)){
            return false;
        }
        for (String pattern : whiteList) {
            if (pathMatcher.match(pattern, requestUri)) {
                return true;
            }
        }
        return false;
    }
}
