# 版本变更日志

## [1.0-SNAPSHOT] - 2024-12-11

### 新增功能

#### 1. Token 认证与鉴权
- 实现基于 JWT 的 Token 校验机制
- 支持白名单配置，灵活控制哪些接口需要认证
- 支持通过配置完全禁用认证功能
- 支持 Ant 风格路径匹配（如 `/api/public/**`）
- 认证日志可配置开关

#### 2. UserContext 用户上下文
- 基于 ThreadLocal 实现的用户上下文管理
- 自动从 JWT Token 中提取并注入用户信息
- 提供便捷的静态方法获取用户信息
- 包含：userId、username、phone、email、userType、traceId

#### 3. Trace ID 链路追踪
- 自动生成或传递 Trace ID
- 支持从 Gateway 传递的 `X-Trace-ID` 请求头获取
- 自动注入到日志 MDC 中，方便日志追踪
- 自动添加到响应头，方便前端追踪
- 与 UserContext 集成

#### 4. 中间件可选引入
- **Redis/Redisson**: 通过 `shopmind.redis.enabled` 控制
- **Seata**: 通过 `shopmind.seata.enabled` 控制
- **RocketMQ**: 通过 `shopmind.rocketmq.enabled` 控制
- 使用条件注解，未启用时不初始化，避免启动报错

#### 5. 日志系统
- 基于 Logback 实现完整的日志解决方案
- 支持控制台和文件双输出
- 按天滚动存储，保留 30 天
- 单独的错误日志文件
- 异步输出，提高性能
- 自动包含 Trace ID
- 支持多环境配置（dev/test/prod）

#### 6. Spring Boot 自动配置
- 实现 Spring Boot 3.x 自动配置机制
- 自动注册拦截器
- 自动扫描组件
- 开箱即用，无需额外配置

### 技术栈

- JDK 17
- Spring Boot 3.2.4
- Spring Cloud 2023.0.1
- Spring Cloud Alibaba 2023.0.1.0
- Redisson 3.27.2
- JJWT 0.12.5
- Hutool 5.8.25
- Lombok 1.18.30

### 依赖管理

- 所有中间件依赖设置为 `optional`，按需引入
- 提供完整的依赖版本管理

### 文档

- README.md: 详细使用文档
- QUICKSTART.md: 快速入门指南
- FEATURES.md: 功能清单
- CHANGELOG.md: 版本变更日志
- application-example.yml: 配置示例

### 构建

- Maven 多模块项目结构
- 父模块：shopmind-common-starter
- 子模块：shopmind-spring-boot-starter

---

## 计划功能

### [1.1.0] - 待定

- [ ] 统一异常处理
- [ ] 统一响应格式
- [ ] 参数校验增强
- [ ] API 限流支持

### [1.2.0] - 待定

- [ ] 分布式锁支持
- [ ] 多级缓存支持
- [ ] Feign 客户端增强
- [ ] 消息队列统一接口

### [2.0.0] - 待定

- [ ] 监控指标集成
- [ ] API 文档自动生成
- [ ] 性能优化
- [ ] 安全增强
