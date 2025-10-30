# OpenFeign 实施说明

## ✅ 已完成的工作

### 1. 在订单服务中接入 OpenFeign

#### 1.1 添加依赖（`coffee-order-service/pom.xml`）

```xml
<!-- OpenFeign -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

#### 1.2 启用 OpenFeign（`OrderServiceApplication.java`）

```java
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients  // ← 添加这个注解
public class OrderServiceApplication {
    // ...
}
```

#### 1.3 创建 Feign 客户端

**用户服务客户端（`UserServiceClient.java`）:**
```java
@FeignClient(name = "coffee-user-service")
public interface UserServiceClient {
    @GetMapping("/user/info")
    Result<Object> getUserInfo();
    
    @GetMapping("/user/{id}")
    Result<Object> getUserById(@PathVariable("id") Long id);
}
```

**商品服务客户端（`ProductServiceClient.java`）:**
```java
@FeignClient(name = "coffee-product-service")
public interface ProductServiceClient {
    @GetMapping("/product/{id}")
    Result<Object> getProductById(@PathVariable("id") Long id);
    
    @GetMapping("/product/list")
    Result<Object> getProductList();
}
```

#### 1.4 配置 Feign 拦截器（`FeignRequestInterceptor.java`）

```java
@Component
public class FeignRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        // 自动传递用户信息到下游服务
        ServletRequestAttributes attributes = 
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String userId = request.getHeader("X-User-Id");
            if (userId != null) {
                template.header("X-User-Id", userId);
            }
        }
    }
}
```

#### 1.5 使用示例（`OrderController.java`）

```java
@RestController
@RequestMapping("/order")
public class OrderController {
    
    @Autowired
    private UserServiceClient userServiceClient;
    
    @Autowired
    private ProductServiceClient productServiceClient;
    
    @GetMapping("/test-feign")
    public Result<Map<String, Object>> testFeign() {
        // 调用用户服务
        Result<Object> userResult = userServiceClient.getUserInfo();
        
        // 调用商品服务
        Result<Object> productResult = productServiceClient.getProductList();
        
        return Result.success(data);
    }
}
```

## 🎯 工作原理

### 调用流程

```
前端 → 网关 → 订单服务
                  ↓
             使用 OpenFeign
                  ↓
         ┌────────┴────────┐
         ↓                 ↓
    用户服务          商品服务
```

### 用户信息传递

1. **网关层**：
   - 验证 JWT 并提取用户信息
   - 添加 `X-User-Id` 和 `X-User-Name` 请求头

2. **微服务层**：
   - `ControllerUtil.getUserId()` 从请求头获取用户ID
   - 或在 Controller 中直接获取

3. **OpenFeign 调用**：
   - `FeignRequestInterceptor` 自动将用户信息传递到下游服务
   - 下游服务可以从请求头中获取用户信息

## 📝 在其他服务中使用 OpenFeign

如果其他服务也需要使用 OpenFeign，按照以下步骤：

### Step 1: 添加依赖

在 `pom.xml` 中添加：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

### Step 2: 启用 OpenFeign

在启动类上添加 `@EnableFeignClients`：

```java
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients  // ← 添加这个注解
public class YourServiceApplication {
    // ...
}
```

### Step 3: 创建 Feign 客户端

```java
@FeignClient(name = "目标服务名称")
public interface YourServiceClient {
    @GetMapping("/api/path")
    Result<Object> yourMethod();
}
```

### Step 4: 在 Service 中使用

```java
@Service
public class YourServiceImpl {
    @Autowired
    private YourServiceClient yourServiceClient;
    
    public void yourMethod() {
        Result<Object> result = yourServiceClient.yourMethod();
        // ...
    }
}
```

## 🔧 配置说明

### Feign 超时配置

在 `application.yml` 中添加：

```yaml
feign:
  client:
    config:
      default:
        # 连接超时（毫秒）
        connectTimeout: 5000
        # 读取超时（毫秒）
        readTimeout: 10000
      # 针对特定服务
      coffee-user-service:
        connectTimeout: 3000
        readTimeout: 5000
```

### Feign 日志配置

在 `application.yml` 中添加：

```yaml
logging:
  level:
    com.coffee.orderservice.client: DEBUG  # Feign客户端包名
```

或者在代码中配置：

```java
@Bean
public Logger.Level feignLoggerLevel() {
    return Logger.Level.FULL;  // NONE, BASIC, HEADERS, FULL
}
```

## 🎨 最佳实践

### 1. 统一异常处理

```java
@Component
public class FeignErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        // 统一处理异常
        return new YourException("服务调用失败");
    }
}
```

### 2. 请求重试

```java
@Bean
public Retryer feignRetryer() {
    // 最大重试次数：3次，初始间隔：100ms，最大间隔：1000ms
    return new Retryer.Default(100, 1000, 3);
}
```

### 3. Hystrix 熔断（可选）

如果使用 Hystrix：

```java
@FeignClient(name = "coffee-user-service", fallback = UserServiceFallback.class)
public interface UserServiceClient {
    // ...
}

@Component
public class UserServiceFallback implements UserServiceClient {
    @Override
    public Result<Object> getUserInfo() {
        return Result.error("服务不可用");
    }
}
```

## 📊 对比：直接调用 vs OpenFeign

| 特性 | 网关转发 | OpenFeign |
|------|---------|-----------|
| 适用场景 | 前端调用后端 | 微服务间调用 |
| 使用方式 | HTTP请求 | 声明式接口 |
| 负载均衡 | ✅ | ✅ |
| 服务发现 | ✅ | ✅ |
| 超时控制 | 手动 | 自动配置 |
| 异常处理 | 手动 | 可统一处理 |

## 🚀 测试

### 1. 启动服务

```bash
cd coffee-microservices
./start-services.sh
```

### 2. 测试 OpenFeign 调用

```bash
# 调用测试接口
curl http://localhost:8089/order/test-feign \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 3. 查看日志

在订单服务的日志中可以看到：

```
INFO  - 测试OpenFeign调用
DEBUG - --> GET http://coffee-user-service/user/info
DEBUG - --> GET http://coffee-product-service/product/list
```

## 📚 参考

- [Spring Cloud OpenFeign 官方文档](https://spring.io/projects/spring-cloud-openfeign)
- [OpenFeign GitHub](https://github.com/OpenFeign/feign)

## ✅ 总结

1. ✅ 已在 `coffee-order-service` 中接入 OpenFeign
2. ✅ 创建了用户服务和商品服务的 Feign 客户端
3. ✅ 配置了自动传递用户信息的拦截器
4. ✅ 提供了完整的使用示例

现在可以在订单服务中使用 OpenFeign 调用其他微服务了！

