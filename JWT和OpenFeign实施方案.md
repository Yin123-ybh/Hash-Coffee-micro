# JWT认证和OpenFeign实施方案

## 一、网关统一JWT认证方案

### 1. 当前问题
- 网关只检查Token是否存在，没有验证Token是否有效
- 每个微服务的Controller层都要重复解析Token获取用户ID
- 服务间调用没有规范的方式

### 2. 改造方案

#### 方案A：网关完整JWT认证 + OpenFeign服务间调用（推荐）

**架构流程：**
```
客户端 → Gateway (JWT验证 + 传递用户信息) → 微服务 (无需再验证)
微服务 → OpenFeign调用 → 其他微服务
```

#### 方案B：网关简单JWT验证 + 各服务独立验证

**架构流程：**
```
客户端 → Gateway (简单验证) → 微服务 (完整验证)
微服务 → RestTemplate调用 → 其他微服务
```

## 二、推荐实现步骤

### Step 1: 添加OpenFeign依赖

在 `pom.xml` 中添加：

```xml
<!-- OpenFeign -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

### Step 2: 网关增强JWT验证

修改 `AuthGlobalFilter.java`：

```java
// 在filter方法中添加JWT验证逻辑
String token = authorization.substring(7);

// 验证JWT是否有效
if (!JwtUtils.validateToken(token)) {
    log.warn("请求路径 {} JWT无效或已过期", path);
    return GatewayConfig.handleError(exchange, "令牌无效或已过期");
}

// 从JWT中提取用户信息并传递给下游服务
String userId = JwtUtils.getUserIdFromToken(token);
String username = JwtUtils.getUsernameFromToken(token);

// 将用户信息添加到请求头中，传递给下游服务
ServerHttpRequest newRequest = request.mutate()
    .header("X-User-Id", userId)
    .header("X-User-Name", username)
    .build();

return chain.filter(exchange.mutate().request(newRequest).build());
```

### Step 3: 修改微服务Controller层

因为网关已经验证了JWT，微服务可以直接从请求头获取用户ID：

```java
// 在需要的微服务中创建工具类
public class RequestContextUtil {
    public static Long getUserId(HttpServletRequest request) {
        String userId = request.getHeader("X-User-Id");
        return userId != null ? Long.valueOf(userId) : null;
    }
}

// 在Controller中使用
@GetMapping("/list")
public Result<List<Address>> getAddressList(HttpServletRequest request) {
    Long userId = RequestContextUtil.getUserId(request);
    // ...业务逻辑
}
```

### Step 4: 使用OpenFeign进行服务间调用

#### 4.1 在需要的微服务中添加OpenFeign依赖并启用

```java
// 在启动类上添加
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients  // 新增
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
```

#### 4.2 创建Feign客户端

```java
// 在order-service中创建UserServiceClient
@FeignClient(name = "coffee-user-service")
public interface UserServiceClient {
    
    @GetMapping("/user/info")
    Result<User> getUserInfo(@RequestHeader("X-User-Id") Long userId);
    
    @GetMapping("/address/list")
    Result<List<Address>> getAddressList(@RequestHeader("X-User-Id") Long userId);
}

// 在OrderService中使用
@Service
public class OrderServiceImpl {
    @Autowired
    private UserServiceClient userServiceClient;
    
    public void createOrder(Long userId, CreateOrderDTO dto) {
        // 通过Feign调用用户服务
        Result<User> userResult = userServiceClient.getUserInfo(userId);
        User user = userResult.getData();
        
        // ... 业务逻辑
    }
}
```

### Step 5: Feign请求传递用户信息

创建Feign拦截器，自动传递用户信息：

```java
@Component
public class FeignRequestInterceptor implements RequestInterceptor {
    
    @Override
    public void apply(RequestTemplate template) {
        // 从当前请求上下文中获取用户信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) 
            RequestContextHolder.getRequestAttributes();
        
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String userId = request.getHeader("X-User-Id");
            String username = request.getHeader("X-User-Name");
            
            if (userId != null) {
                template.header("X-User-Id", userId);
            }
            if (username != null) {
                template.header("X-User-Name", username);
            }
        }
    }
}
```

## 三、优势总结

### 优势
1. **统一认证**：网关统一验证JWT，微服务无需重复验证
2. **性能提升**：避免每个请求都重复验证JWT
3. **代码简化**：微服务只需从请求头获取用户信息
4. **服务解耦**：使用OpenFeign进行声明式服务调用
5. **易于维护**：认证逻辑集中管理

### 安全考虑
1. **Token有效性验证**：在网关验证Token是否过期、签名是否正确
2. **请求头传递**：使用自定义请求头传递用户信息，不暴露在URL中
3. **HTTPS传输**：生产环境建议使用HTTPS

## 四、实施建议

### 实施步骤
1. 第一步：先完善网关JWT验证（必须）
2. 第二步：添加OpenFeign依赖
3. 第三步：修改Controller获取用户信息的方式
4. 第四步：创建Feign客户端替代直接调用
5. 第五步：添加Feign拦截器自动传递用户信息

### 注意事项
1. 需要JwtUtils工具类支持验证Token
2. 请求头名称要统一（如：X-User-Id, X-User-Name）
3. 需要处理用户信息不存在的情况
4. 要考虑服务间调用的超时和重试

## 五、后续优化

1. **统一异常处理**：处理JWT验证失败、服务调用失败等异常
2. **请求日志**：记录JWT验证和服务调用日志
3. **性能监控**：监控JWT验证和服务调用的耗时
4. **熔断降级**：使用Sentinel或Hystrix进行服务降级
