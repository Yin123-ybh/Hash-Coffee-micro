# ☕️ 咖啡点餐系统 - 微服务架构

## 📋 项目概述

这是一个基于Spring Cloud微服务架构的智能咖啡点餐系统，将原有的单体应用拆分为多个独立的微服务，提供更好的可扩展性、可维护性和高可用性。

## 🏗️ 微服务架构

### 服务拆分

| 服务名称 | 端口 | 职责 | 数据库 |
|---------|------|------|--------|
| coffee-gateway | 8080 | API网关，统一入口 | - |
| coffee-user-service | 8081 | 用户管理、认证授权 | coffee_user_db |
| coffee-product-service | 8082 | 商品管理、分类管理 | coffee_product_db |
| coffee-order-service | 8083 | 订单管理、支付处理 | coffee_order_db |
| coffee-coupon-service | 8084 | 优惠券管理、秒杀活动 | coffee_coupon_db |
| coffee-cart-service | 8085 | 购物车管理 | coffee_cart_db |
| coffee-ai-service | 8086 | AI智能推荐、数据分析 | coffee_ai_db |
| coffee-statistics-service | 8087 | 数据统计分析 | coffee_statistics_db |

### 技术栈

- **服务注册发现**: Nacos
- **API网关**: Spring Cloud Gateway
- **负载均衡**: Spring Cloud LoadBalancer
- **数据库**: MySQL
- **缓存**: Redis
- **消息队列**: RabbitMQ
- **文档**: Knife4j (Swagger)

## 🚀 快速启动

### 1. 环境准备

```bash
# 确保已安装以下软件
- JDK 8+
- Maven 3.6+
- MySQL 5.7+
- Redis 6.0+
- Nacos 2.2.3+
```

### 2. 启动Nacos

```bash
# 下载并启动Nacos Server
wget https://github.com/alibaba/nacos/releases/download/2.2.3/nacos-server-2.2.3.tar.gz
tar -xzf nacos-server-2.2.3.tar.gz
cd nacos/bin
sh startup.sh -m standalone

# 访问Nacos控制台
# URL: http://localhost:8848/nacos
# 用户名/密码: nacos/nacos
```

### 3. 创建数据库

```bash
# 执行数据库脚本
mysql -u root -p < database-scripts/create-databases.sql
```

### 4. 启动微服务

```bash
# 编译项目
mvn clean install

# 启动网关服务
cd coffee-gateway
mvn spring-boot:run

# 启动用户服务
cd coffee-user-service
mvn spring-boot:run

# 启动商品服务
cd coffee-product-service
mvn spring-boot:run

# 其他服务类似启动...
```

## 📊 服务监控

### Nacos控制台
- 服务列表: http://localhost:8848/nacos/#/serviceManagement
- 配置管理: http://localhost:8848/nacos/#/configurationManagement

### API文档
- 用户服务: http://localhost:8081/doc.html
- 商品服务: http://localhost:8082/doc.html
- 其他服务类似...

## 🔧 配置说明

### 数据库配置
每个微服务都有独立的数据库，配置文件位于各服务的 `application.yml` 中。

### Redis配置
各服务使用不同的Redis数据库：
- 用户服务: database: 0
- 商品服务: database: 1
- 订单服务: database: 2
- 其他服务依次递增

### 服务注册
所有服务都注册到Nacos，配置文件位于各服务的 `bootstrap.yml` 中。

## 🔄 服务调用

### 通过网关调用
```
# 用户服务
GET http://localhost:8080/user/list

# 商品服务
GET http://localhost:8080/product/list

# 订单服务
GET http://localhost:8080/order/list
```

### 直接调用微服务
```
# 用户服务
GET http://localhost:8081/user/list

# 商品服务
GET http://localhost:8082/product/list

# 订单服务
GET http://localhost:8083/order/list
```

## 🛠️ 开发指南

### 添加新微服务

1. 在根pom.xml中添加新的module
2. 创建新的微服务目录结构
3. 配置服务注册和数据库连接
4. 在网关中添加路由配置

### 服务间通信

```java
// 使用RestTemplate进行服务调用
@Autowired
private RestTemplate restTemplate;

public User getUserById(Long id) {
    return restTemplate.getForObject("http://coffee-user-service/user/" + id, User.class);
}
```

## 📈 性能优化

### 缓存策略
- 商品信息缓存
- 用户信息缓存
- 订单状态缓存

### 数据库优化
- 读写分离
- 分库分表
- 索引优化

### 服务优化
- 服务熔断
- 限流控制
- 负载均衡

## 🔒 安全配置

### JWT认证
- 统一认证中心
- 令牌刷新机制
- 权限控制

### 网关安全
- 请求限流
- IP白名单
- 敏感信息过滤

## 📝 部署说明

### Docker部署
```bash
# 构建镜像
docker build -t coffee-user-service .

# 运行容器
docker run -d -p 8081:8081 coffee-user-service
```

### Kubernetes部署
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: coffee-user-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: coffee-user-service
  template:
    metadata:
      labels:
        app: coffee-user-service
    spec:
      containers:
      - name: coffee-user-service
        image: coffee-user-service:latest
        ports:
        - containerPort: 8081
```

## 🐛 常见问题

### 服务注册失败
1. 检查Nacos是否启动
2. 检查网络连接
3. 检查配置文件

### 服务调用失败
1. 检查服务是否注册成功
2. 检查网关路由配置
3. 检查服务健康状态

### 数据库连接失败
1. 检查数据库是否启动
2. 检查连接配置
3. 检查数据库权限

## 📞 技术支持

如有问题，请联系开发团队或提交Issue。

---

**注意**: 这是一个演示项目，生产环境部署时请根据实际情况调整配置。

