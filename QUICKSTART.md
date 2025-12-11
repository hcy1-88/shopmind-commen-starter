# 快速入门指南

本指南帮助你快速开始使用 Shopmind Common Starter。

## 1. 添加依赖

在你的业务项目 `pom.xml` 中添加：

```xml
<dependency>
    <groupId>com.hcy</groupId>
    <artifactId>shopmind-spring-boot-starter</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## 2. 最小化配置

在 `application.yml` 中添加最基本的配置：

```yaml
spring:
  application:
    name: your-service-name

shopmind:
  auth:
    jwt-secret: your-jwt-secret-key-min-32-chars
    whitelist:
      - /api/auth/**
      - /api/public/**
```

就这么简单！启动你的应用即可。

## 3. 使用用户上下文

在你的 Controller 或 Service 中：

```java
import com.hcy.shopmind.common.context.UserContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/profile")
    public Map<String, Object> getProfile() {
        // 获取当前登录用户信息
        Long userId = UserContext.userId();
        String username = UserContext.username();

        // 日志会自动包含 TraceId
        log.info("用户查询个人信息, userId={}", userId);

        return Map.of(
            "userId", userId,
            "username", username
        );
    }
}
```

## 4. 测试

### 4.1 测试白名单接口（无需 Token）

```bash
curl http://localhost:8080/api/public/test
```

### 4.2 测试需要认证的接口（需要 Token）

```bash
curl -H "Authorization: Bearer your-jwt-token" \
     http://localhost:8080/api/user/profile
```

## 5. 查看日志

日志文件位置：`./logs/your-service-name.log`

日志格式示例：
```
2024-12-11 19:00:00.123 [http-nio-8080-exec-1] [abc123def456] INFO  c.h.s.UserController - 用户查询个人信息, userId=12345
```

其中 `[abc123def456]` 就是 TraceId，可以用来追踪整个请求链路。

## 6. 启用中间件（可选）

### 启用 Redis

```yaml
shopmind:
  redis:
    enabled: true

spring:
  data:
    redis:
      host: localhost
      port: 6379
```

### 启用 Seata

```yaml
shopmind:
  seata:
    enabled: true
```

### 启用 RocketMQ

```yaml
shopmind:
  rocketmq:
    enabled: true
```

## 7. 环境配置

### 开发环境（dev）

```yaml
spring:
  profiles:
    active: dev

# dev 环境日志只输出到控制台
```

### 生产环境（prod）

```yaml
spring:
  profiles:
    active: prod

# prod 环境日志只输出到文件
shopmind:
  log:
    path: /var/log/shopmind
```

## 常见场景

### 场景1：Gateway 服务（不需要认证）

```yaml
shopmind:
  auth:
    enabled: false  # 禁用认证
```

### 场景2：公开 API 服务（部分接口需要认证）

```yaml
shopmind:
  auth:
    enabled: true
    whitelist:
      - /api/public/**
      - /api/auth/**
      - /swagger-ui/**
```

### 场景3：内部服务（全部需要认证）

```yaml
shopmind:
  auth:
    enabled: true
    whitelist:
      - /actuator/health  # 只开放健康检查
```

## 下一步

查看 [README.md](README.md) 了解更多详细功能和配置选项。
