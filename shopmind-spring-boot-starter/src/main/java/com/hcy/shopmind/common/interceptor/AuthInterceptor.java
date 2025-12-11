package com.hcy.shopmind.common.interceptor;

import cn.hutool.core.util.StrUtil;
import com.hcy.shopmind.common.constant.CommonConstants;
import com.hcy.shopmind.common.context.UserContext;
import com.hcy.shopmind.common.properties.AuthProperties;
import com.hcy.shopmind.common.util.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 认证拦截器
 * 负责 Token 校验和用户信息注入
 */
@Slf4j
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final AuthProperties authProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestUri = request.getRequestURI();

        // 检查是否在白名单中
        if (isInWhitelist(requestUri)) {
            if (authProperties.isLogEnabled()) {
                log.debug("请求路径在白名单中，跳过认证: {}", requestUri);
            }
            return true;
        }

        // 获取 Token
        String token = request.getHeader(CommonConstants.AUTHORIZATION_HEADER);
        if (StrUtil.isBlank(token)) {
            log.warn("请求未携带 Token: {}", requestUri);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        // 解析 Token
        Claims claims = JwtUtils.parseToken(token, authProperties.getJwtSecret());
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
        Long userId = JwtUtils.getClaimAsLong(claims, CommonConstants.JWT_USER_ID);
        String username = JwtUtils.getClaimAsString(claims, CommonConstants.JWT_USERNAME);
        String phone = JwtUtils.getClaimAsString(claims, CommonConstants.JWT_PHONE);
        String email = JwtUtils.getClaimAsString(claims, CommonConstants.JWT_EMAIL);
        Integer userType = JwtUtils.getClaimAsInteger(claims, CommonConstants.JWT_USER_TYPE);

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
     * 检查请求路径是否在白名单中
     */
    private boolean isInWhitelist(String requestUri) {
        for (String pattern : authProperties.getWhitelist()) {
            if (pathMatcher.match(pattern, requestUri)) {
                return true;
            }
        }
        return false;
    }
}
