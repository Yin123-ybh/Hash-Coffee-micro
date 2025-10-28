package com.coffee.orderservice.service.impl;

import com.coffee.common.result.Result;
import com.coffee.orderservice.client.ProductServiceClient;
import com.coffee.orderservice.client.UserServiceClient;
import com.coffee.orderservice.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 订单服务实现类
 * 职责：实现订单业务逻辑
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private ProductServiceClient productServiceClient;

    @Override
    public Map<String, Object> createOrder(Map<String, Object> orderData, Long userId) throws IllegalArgumentException {
        log.info("开始创建订单，userId={}", userId);

        // 1. 参数校验
        validateOrderData(orderData);

        // 2. 解析订单数据
        List<Map<String, Object>> products = parseProducts(orderData);
        Long addressId = parseAddressId(orderData);

        // 3. 使用OpenFeign调用其他服务进行业务验证
        validateOrderWithOtherServices(userId, addressId, products);

        // 4. 生成订单号
        String orderNo = generateOrderNo();

        // 5. 计算总价（优先使用前端传递的totalAmount，否则计算）
        double totalPrice;
        if (orderData.containsKey("totalAmount")) {
            totalPrice = getDoubleValue(orderData.get("totalAmount"));
            log.info("使用前端传递的总价: {}", totalPrice);
        } else {
            totalPrice = calculateTotalPrice(products);
            log.info("计算的总价: {}", totalPrice);
        }

        // 6. 构建订单对象
        Map<String, Object> order = buildOrder(orderNo, userId, addressId, totalPrice, orderData);

        log.info("订单创建成功，orderNo={}, userId={}, totalPrice={}", orderNo, userId, totalPrice);

        return order;
    }

    /**
     * 使用OpenFeign调用其他服务进行业务验证
     */
    private void validateOrderWithOtherServices(Long userId, Long addressId, List<Map<String, Object>> products) throws IllegalArgumentException {
        try {
            // 暂时跳过地址验证，后续需要实现地址验证接口
            log.info("验证地址: addressId={}, userId={}", addressId, userId);

            //  调用用户服务验证地址是否存在

            Result<Object> addressResult = userServiceClient.getAddressById(addressId);
            if (addressResult == null || !addressResult.isSuccess() || addressResult.getData() == null) {
                throw new IllegalArgumentException("收货地址无效或不存在");
            }
            // 暂时只做日志记录
            if (addressId == null || addressId <= 0) {
                throw new IllegalArgumentException("请选择正确的收货地址");
            }

            // 验证商品信息
            log.info("验证商品信息，商品数量: {}", products.size());
            for (Map<String, Object> product : products) {
                // 支持productId和id两种字段名
                Object productId = product.get("productId") != null ? product.get("productId") : product.get("id");
                if (productId == null) {
                    throw new IllegalArgumentException("商品ID不能为空");
                }

                // 调用商品服务校验商品是否存在
                Result<Object> productResult = productServiceClient.getProductById(Long.valueOf(productId.toString()));
                if (productResult == null || !productResult.isSuccess() || productResult.getData() == null) {
                    throw new IllegalArgumentException("商品ID " + productId + " 不存在");
                }
                log.info("验证商品: productId={}, quantity={}", productId, product.get("quantity"));
            }

            log.info("订单数据验证通过");

        } catch (Exception e) {
            log.error("调用其他服务验证失败: {}", e.getMessage());
            if (e instanceof IllegalArgumentException) {
                throw e;
            }
            throw new IllegalArgumentException("订单验证失败，请稍后重试");
        }
    }

    /**
     * 校验订单数据
     */
    private void validateOrderData(Map<String, Object> orderData) throws IllegalArgumentException {
        if (orderData == null) {
            throw new IllegalArgumentException("订单数据不能为空");
        }

        // 校验商品列表（支持items或products两种字段名）
        Object productsObj = orderData.get("items") != null ? orderData.get("items") : orderData.get("products");
        if (productsObj == null) {
            log.error("订单数据: {}", orderData);
            throw new IllegalArgumentException("商品列表不能为空");
        }

        if (!(productsObj instanceof List)) {
            throw new IllegalArgumentException("商品列表格式错误");
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> products = (List<Map<String, Object>>) productsObj;
        if (products.isEmpty()) {
            throw new IllegalArgumentException("商品列表不能为空");
        }

        // 校验地址ID
        Object addressIdObj = orderData.get("addressId");
        if (addressIdObj == null) {
            throw new IllegalArgumentException("收货地址不能为空");
        }
    }

    /**
     * 解析商品列表
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> parseProducts(Map<String, Object> orderData) {
        // 支持items和products两种字段名
        if (orderData.get("items") != null) {
            return (List<Map<String, Object>>) orderData.get("items");
        }
        return (List<Map<String, Object>>) orderData.get("products");
    }

    /**
     * 解析地址ID
     */
    private Long parseAddressId(Map<String, Object> orderData) {
        Object addressIdObj = orderData.get("addressId");
        if (addressIdObj instanceof Number) {
            return ((Number) addressIdObj).longValue();
        }
        return Long.valueOf(addressIdObj.toString());
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }

    /**
     * 计算总价
     */
    private double calculateTotalPrice(List<Map<String, Object>> products) {
        double totalPrice = 0.0;
        for (Map<String, Object> product : products) {
            Object quantityObj = product.get("quantity");
            Object priceObj = product.get("price");
            if (quantityObj != null && priceObj != null) {
                int quantity = getIntValue(quantityObj);
                double price = getDoubleValue(priceObj);
                totalPrice += quantity * price;
            }
        }
        return totalPrice;
    }

    /**
     * 构建订单对象
     */
    private Map<String, Object> buildOrder(String orderNo, Long userId, Long addressId, double totalPrice, Map<String, Object> orderData) {
        Map<String, Object> order = new HashMap<>();
        order.put("id", System.currentTimeMillis()); // 临时ID，后续从数据库生成
        order.put("orderNo", orderNo);
        order.put("userId", userId);
        order.put("addressId", addressId);
        order.put("totalPrice", totalPrice);
        order.put("deliveryFee", orderData.getOrDefault("deliveryFee", 5.0)); // 默认配送费5元
        order.put("status", 1); // 1-待支付（匹配前端statusMap）
        order.put("createTime", new Date());

        // 添加其他需要的字段
        order.put("payAmount", totalPrice + 5.0); // 实付金额 = 总价 + 配送费
        order.put("remark", orderData.getOrDefault("remark", ""));

        return order;
    }

    /**
     * 转换为整型
     */
    private int getIntValue(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return Integer.parseInt(value.toString());
    }

    /**
     * 转换为双精度浮点型
     */
    private double getDoubleValue(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return Double.parseDouble(value.toString());
    }
}