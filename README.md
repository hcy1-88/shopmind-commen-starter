# Shopmind Common Starter

Shopmind AI 电商微服务项目的基础框架，基于 Spring Cloud Alibaba 2023.0.1.0 构建。

## 技术栈

- JDK 17
- Spring Boot 3.2.4
- Spring Cloud 2023.0.1
- Spring Cloud Alibaba 2023.0.1.0

## 功能特性

### 1. Token 认证与鉴权

- 可通过配置开关完全禁用认证，默认启用认证（如果你的业务存在需认证的接口，建议开启）
- 支持白名单配置（不需要认证的接口，通常配一些系统接口）
- 只认证被 @RequireAuth 注解的类或url接口
- 基于 JWT 的 Token 校验
- 自动解析 Token 并注入用户上下文


### 2. UserContext 用户上下文

- 基于 ThreadLocal 实现的用户上下文
- 自动从 Token 中提取用户信息
- 提供便捷的静态方法获取用户信息

```java
// 获取当前用户信息
Long userId = UserContext.userId();
String username = UserContext.username();
String phone = UserContext.phone();
String email = UserContext.email();
Integer userType = UserContext.userType();
String traceId = UserContext.traceId();
```

### 3. Trace ID 链路追踪

- 自动生成或传递 Trace ID
- 从 Gateway 传递的 `X-Trace-ID` 请求头获取
- 如果请求未携带，自动生成新的 Trace ID
- 自动注入到日志 MDC 中，方便日志追踪
- 自动添加到响应头，方便前端追踪


### 4. 日志系统

- 基于 Logback 实现
- 支持控制台和文件双输出
- 按天滚动存储日志文件
- 自动包含 Trace ID，方便链路追踪
- 单独的错误日志文件
- 异步输出，提高性能
- 支持不同环境的日志配置（spring.profiles.active=dev/test/prod）

## 快速开始

### 1. 引入依赖

在业务项目的 `pom.xml` 中引入：

```xml
<dependency>
    <groupId>com.hcy</groupId>
    <artifactId>shopmind-spring-boot-starter</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### 2. 配置文件

在 `application.yml` 中添加配置：

```yaml
spring:
  application:
    name: user-service

shopmind:
  auth:
    enabled: true
    jwt-secret: your-jwt-secret-key
    whitelist:
      - /api/auth/login
      - /api/auth/register
      - /api/public/**
  log:
    path: ./logs
```

### 3. 使用示例

#### 3.1 获取用户信息

```java
@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/profile")
    public String getProfile() {
        Long userId = UserContext.userId();
        String username = UserContext.username();
        return "User: " + username + ", ID: " + userId;
    }
}
```

#### 3.2 日志打印（自动包含 Trace ID）

```java
@Slf4j
@Service
public class UserService {

    public void doSomething() {
        // 日志会自动包含 [TraceID]
        log.info("处理用户请求, userId={}", UserContext.userId());
    }
}
```

## 配置说明

### 认证配置

| 配置项 | 说明 | 默认值 | 必填 |
|-------|------|-------|------|
| `shopmind.auth.enabled` | 是否启用认证 | `true` | 否 |
| `shopmind.auth.jwt-secret` | JWT 密钥 | 默认密钥（生产环境必须修改） | 是 |
| `shopmind.auth.log-enabled` | 是否打印认证日志 | `true` | 否 |
| `shopmind.auth.whitelist` | 白名单路径列表 | 见示例配置 | 否 |

### 日志配置

| 配置项 | 说明 | 默认值 | 必填 |
|-------|------|-------|------|
| `shopmind.log.path` | 日志文件存储路径 | `./logs` | 否 |

### 中间件配置

| 配置项 | 说明 | 默认值 | 必填 |
|-------|------|-------|------|
| `shopmind.redis.enabled` | 是否启用 Redis | `false` | 否 |
| `shopmind.seata.enabled` | 是否启用 Seata | `false` | 否 |
| `shopmind.rocketmq.enabled` | 是否启用 RocketMQ | `false` | 否 |

## 日志格式

日志格式包含以下信息：

```
2024-12-11 19:00:00.123 [http-nio-8080-exec-1] [abc123def456] INFO  c.h.s.UserService - 处理用户请求, userId=12345
```

格式说明：
- `2024-12-11 19:00:00.123`：时间戳
- `[http-nio-8080-exec-1]`：线程名
- `[abc123def456]`：Trace ID
- `INFO`：日志级别
- `c.h.s.UserService`：Logger 名称
- `处理用户请求, userId=12345`：日志内容

## 日志文件

日志文件按以下规则存储：

- 所有日志：`${log.path}/${spring.application.name}.log`
- 错误日志：`${log.path}/${spring.application.name}-error.log`
- 历史日志：`${log.path}/${spring.application.name}.yyyy-MM-dd.log`

日志保留策略：
- 保留 30 天的历史日志
- 总大小限制 10GB（所有日志）/ 5GB（错误日志）

## 注意事项

1. **JWT 密钥**：生产环境务必修改 `shopmind.auth.jwt-secret`，建议使用 32 位以上的随机字符串
2. **白名单配置**：根据实际业务需求配置白名单路径，支持 Ant 风格的路径匹配（如 `/api/public/**`）
3. **中间件启用**：只在需要时启用中间件，避免不必要的资源消耗
4. **日志路径**：确保应用有权限写入日志路径
5. **Trace ID 传递**：Gateway 应该在请求头中添加 `X-Trace-ID`，如果没有则由各服务自动生成


## 开发指南

### 扩展白名单

在业务项目的配置文件中添加：

```yaml
shopmind:
  auth:
    whitelist:
      - /api/auth/login
      - /api/auth/register
      - /api/public/**
      - /your/custom/path/**
```

### 禁用认证

如果某个服务不需要认证（如 Gateway），可以禁用：

```yaml
shopmind:
  auth:
    enabled: false
```


### 3. 日志文件没有生成？

检查：
1. 日志路径是否有写入权限
2. 应用是否配置了 `spring.application.name`
3. 是否配置了环境（如 `spring.profiles.active`, dev 输出控制台，test 和 prod 输出到磁盘文件）

### 4. Trace ID 没有打印？

确保使用了 `logback-spring.xml` 配置文件，并且日志格式中包含 `%X{X-Trace-ID}`。

## 许可证

Copyright © 2024 Shopmind
