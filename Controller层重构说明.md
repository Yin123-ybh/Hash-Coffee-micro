# Controller层重构说明

## 重构原则
Controller层只负责：接收请求参数、调用Service、返回响应结果
业务逻辑必须放在Service层处理

## 发现的问题

### 1. UserCouponController
**问题：**
- Controller直接注入Mapper，跳过了Service层
- 在Controller中进行数据过滤（stream().filter()）
- 直接操作业务逻辑

**修改：**
- 创建了 `UserCouponService` 接口和 `UserCouponServiceImpl` 实现
- 将过滤逻辑移到Service层
- Controller只负责调用Service

### 2. AddressController 和 UserController
**问题：**
- 每个方法都重复编写获取用户ID的逻辑
- Token解析代码重复
- 代码冗余

**修改：**
- 创建了 `ControllerUtil.getUserId()` 工具方法
- 所有Controller统一使用工具方法获取用户ID
- 代码从10行+缩减到2行

## 重构后的代码结构

### Controller层职责
1. 接收HTTP请求参数
2. 获取用户ID（统一工具方法）
3. 调用Service层方法
4. 返回统一的Result响应

### Service层职责
1. 处理业务逻辑
2. 调用Mapper层查询数据
3. 进行数据过滤、转换、组合
4. 返回业务结果

### 工具类
`ControllerUtil.getUserId()` - 统一处理用户身份验证

## 重构示例

### 重构前
```java
@GetMapping("/list")
public Result<List<Address>> getAddressList(HttpServletRequest request) {
    try {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.unauthorized("未授权访问");
        }
        
        token = token.substring(7);
        String userIdStr = JwtUtils.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.unauthorized("令牌无效");
        }
        
        Long userId = Long.valueOf(userIdStr);
        List<Address> addresses = addressService.getAddressList(userId);
        return Result.success(addresses);
    } catch (Exception e) {
        log.error("获取地址列表失败: {}", e.getMessage());
        return Result.error(e.getMessage());
    }
}
```

### 重构后
```java
@GetMapping("/list")
public Result<List<Address>> getAddressList(HttpServletRequest request) {
    try {
        Long userId = ControllerUtil.getUserId(request);
        if (userId == null) {
            return Result.unauthorized("未授权访问");
        }
        
        List<Address> addresses = addressService.getAddressList(userId);
        return Result.success(addresses);
    } catch (Exception e) {
        log.error("获取地址列表失败: {}", e.getMessage());
        return Result.error(e.getMessage());
    }
}
```

## 新增文件
1. `coffee-user-service/src/main/java/com/coffee/userservice/util/ControllerUtil.java`
2. `coffee-coupon-service/src/main/java/com/coffee/couponservice/service/UserCouponService.java`
3. `coffee-coupon-service/src/main/java/com/coffee/couponservice/service/impl/UserCouponServiceImpl.java`

## 修改的文件
1. `AddressController.java` - 简化用户ID获取逻辑
2. `UserCouponController.java` - 移除业务逻辑，调用Service层
3. `UserController.java` - 可以进一步优化，提取获取用户ID的逻辑

## 后续优化建议
1. 可以为UserController创建Service层（目前看起来逻辑较简单）
2. 考虑创建统一的AOP切面处理用户认证
3. 可以考虑使用拦截器统一处理用户ID获取
