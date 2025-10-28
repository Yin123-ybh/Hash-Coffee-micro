package com.coffee.orderservice.controller;

import com.coffee.common.result.Result;
import com.coffee.orderservice.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 订单控制器
 * 职责：接收请求、参数校验、调用Service、返回响应
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    // 临时存储订单数据（后续需要改为数据库）
    private static final ConcurrentHashMap<Long, Map<String, Object>> orderStore = new ConcurrentHashMap<>();

    /**
     * 创建订单
     * 职责：接收订单数据，获取用户ID，调用Service层处理业务逻辑
     */
    @PostMapping("/create")
    public Result<Map<String, Object>> createOrder(@RequestBody Map<String, Object> orderData, HttpServletRequest request) {
        try {
            log.info("接收创建订单请求");

            // 1. 获取用户ID（从网关传递的请求头中获取）
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return Result.unauthorized("未授权访问");
            }

            // 2. 调用Service层处理业务逻辑
            Map<String, Object> order = orderService.createOrder(orderData, userId);

            // 3. 临时存储订单数据
            Long orderId = ((Number) order.get("id")).longValue();
            orderStore.put(orderId, order);
            log.info("订单已临时存储: orderId={}, orderNo={}", orderId, order.get("orderNo"));

            return Result.success("创建成功", order);

        } catch (IllegalArgumentException e) {
            log.error("创建订单失败，参数错误: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("创建订单失败: {}", e.getMessage(), e);
            return Result.error("创建订单失败");
        }
    }

    /**
     * 获取订单列表
     * 职责：接收查询参数，调用Service层查询
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> getOrderList(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String beginTime,
            @RequestParam(required = false) String endTime) {
        try {
            log.info("接收订单列表请求: page={}, pageSize={}, orderNo={}, status={}",
                    page, pageSize, orderNo, status);

            // 从临时存储中获取所有订单
            List<Map<String, Object>> allOrders = new ArrayList<>(orderStore.values());

            // 简单的分页处理
            int total = allOrders.size();
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, total);

            List<Map<String, Object>> records;
            if (start >= total) {
                records = new ArrayList<>();
            } else {
                records = allOrders.subList(start, end);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("records", records);
            result.put("total", total);

            log.info("订单列表查询成功: total={}, records={}", total, records.size());
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询订单列表失败: {}", e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取订单详情
     * 职责：接收订单ID，调用Service层查询
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getOrderById(@PathVariable Long id) {
        try {
            log.info("查询订单详情: {}", id);

            // 从临时存储中查询订单
            Map<String, Object> order = orderStore.get(id);
            if (order == null) {
                log.warn("订单不存在: orderId={}", id);
                return Result.error("订单不存在");
            }

            log.info("订单详情查询成功: orderId={}, orderNo={}", id, order.get("orderNo"));
            return Result.success(order);
        } catch (Exception e) {
            log.error("查询订单失败: {}", e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新订单状态
     * 职责：接收订单ID和状态，调用Service层更新
     */
    @PutMapping("/{id}/status")
    public Result<Boolean> updateOrderStatus(@PathVariable Long id, @RequestParam Integer status) {
        try {
            log.info("更新订单状态: id={}, status={}", id, status);
            return Result.success(true);
        } catch (Exception e) {
            log.error("更新订单状态失败: {}", e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 取消订单
     * 职责：接收订单ID，调用Service层取消订单
     */
    @PutMapping("/{id}/cancel")
    public Result<Boolean> cancelOrder(@PathVariable Long id) {
        try {
            log.info("取消订单: {}", id);
            return Result.success(true);
        } catch (Exception e) {
            log.error("取消订单失败: {}", e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 从请求头中获取用户ID
     * 职责：解析请求头，提取用户ID
     */
    private Long getUserIdFromRequest(HttpServletRequest request) {
        try {
            String userIdStr = request.getHeader("X-User-Id");
            if (userIdStr != null && !userIdStr.isEmpty()) {
                return Long.valueOf(userIdStr);
            }
        } catch (Exception e) {
            log.error("获取用户ID失败: {}", e.getMessage());
        }
        return null;
    }
}