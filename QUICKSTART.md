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

```

就这么简单！启动你的应用即可。

## 3. 使用用户上下文

在你的 Controller 或 Service 中：

```java
import context.com.shopmind.UserContext;
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
