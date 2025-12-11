package com.hcy.shopmind.common.annotation;

import java.lang.annotation.*;

/**
 * 需要认证的注解
 * 用于标记需要用户登录才能访问的接口
 *
 * <p>使用方式：</p>
 * <pre>
 * // 1. 标记在控制器类上，表示该控制器所有方法都需要认证
 * {@code @RequireAuth}
 * {@code @RestController}
 * public class OrderController {
 *     // 所有方法都需要认证
 * }
 *
 * // 2. 标记在方法上，表示该方法需要认证
 * {@code @RestController}
 * public class ProductController {
 *     public List<Product> list() {
 *         // 公开接口，不需要认证
 *     }
 *
 *     {@code @RequireAuth}
 *     public void addToFavorites() {
 *         // 需要登录才能访问
 *     }
 * }
 * </pre>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireAuth {
}