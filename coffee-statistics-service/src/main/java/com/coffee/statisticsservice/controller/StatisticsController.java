package com.coffee.statisticsservice.controller;

import com.coffee.common.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 统计控制器
 */
@RestController
@RequestMapping("/statistics")
public class StatisticsController {
    
    private static final Logger log = LoggerFactory.getLogger(StatisticsController.class);
    
    @GetMapping
    public Result<Map<String, Object>> getStatisticsData(
            @RequestParam(required = false, defaultValue = "7days") String timeRange) {
        try {
            log.info("接收统计数据请求: timeRange={}", timeRange);
            
            Map<String, Object> result = new HashMap<>();
            
            // 营业额数据（模拟）
            Map<String, Object> revenueData = new HashMap<>();
            revenueData.put("dates", new String[]{"10/20", "10/21", "10/22", "10/23", "10/24", "10/25", "10/26"});
            revenueData.put("values", new int[]{580, 620, 750, 680, 820, 900, 850});
            result.put("revenueData", revenueData);
            
            // 用户增长数据（模拟）
            Map<String, Object> userData = new HashMap<>();
            userData.put("dates", new String[]{"10/20", "10/21", "10/22", "10/23", "10/24", "10/25", "10/26"});
            userData.put("values", new int[]{12, 18, 25, 20, 30, 35, 28});
            result.put("userData", userData);
            
            // 订单状态分布（模拟）
            Map<String, Object> orderData = new HashMap<>();
            orderData.put("pending", 5);
            orderData.put("paid", 32);
            orderData.put("completed", 28);
            orderData.put("cancelled", 3);
            result.put("orderData", orderData);
            
            // 商品销量排名（模拟）
            Map<String, Object> salesData = new HashMap<>();
            salesData.put("products", new String[]{"美式咖啡", "拿铁咖啡", "卡布奇诺", "摩卡咖啡", "焦糖玛奇朵"});
            salesData.put("values", new int[]{125, 98, 76, 54, 42});
            result.put("salesData", salesData);
            
            log.info("返回统计数据");
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询统计数据失败: {}", e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }
}

