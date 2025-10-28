package com.coffee.couponservice.config;

import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.CircuitBreakerStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Sentinel流量控制和熔断降级配置
 * 
 * 功能说明：
 * 1. 流量控制：限制接口的QPS，防止系统过载
 * 2. 熔断降级：当服务异常率过高时，自动熔断保护系统
 * 3. 异常处理：提供友好的降级响应
 */
@Configuration
public class SentinelConfig {
    
    /**
     * 配置Sentinel资源切面
     * 用于拦截@SentinelResource注解的方法
     */
    @Bean
    public SentinelResourceAspect sentinelResourceAspect() {
        return new SentinelResourceAspect();
    }
    
    /**
     * 配置RestTemplate
     * 用于服务间调用
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    /**
     * 初始化流量控制规则
     * 在应用启动时自动加载规则
     */
    @PostConstruct
    public void initFlowRules() {
        initFlowControlRules();
        initDegradeRules();
    }
    
    /**
     * 初始化流量控制规则
     */
    private void initFlowControlRules() {
        List<FlowRule> rules = new ArrayList<>();
        
        // 1. 优惠券秒杀接口限流规则
        FlowRule seckillRule = new FlowRule();
        seckillRule.setResource("seckillCoupon");
        seckillRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        seckillRule.setCount(100); // 每秒允许100个请求
        seckillRule.setStrategy(RuleConstant.STRATEGY_DIRECT);
        seckillRule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT);
        rules.add(seckillRule);
        
        // 2. 优惠券查询接口限流规则
        FlowRule queryRule = new FlowRule();
        queryRule.setResource("queryCoupon");
        queryRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        queryRule.setCount(200); // 每秒允许200个请求
        queryRule.setStrategy(RuleConstant.STRATEGY_DIRECT);
        queryRule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT);
        rules.add(queryRule);
        
        // 3. 用户优惠券查询接口限流规则
        FlowRule userCouponRule = new FlowRule();
        userCouponRule.setResource("getUserCoupons");
        userCouponRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        userCouponRule.setCount(150); // 每秒允许150个请求
        userCouponRule.setStrategy(RuleConstant.STRATEGY_DIRECT);
        userCouponRule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT);
        rules.add(userCouponRule);
        
        // 4. 优惠券领取接口限流规则
        FlowRule claimRule = new FlowRule();
        claimRule.setResource("claimCoupon");
        claimRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        claimRule.setCount(50); // 每秒允许50个请求
        claimRule.setStrategy(RuleConstant.STRATEGY_DIRECT);
        claimRule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT);
        rules.add(claimRule);
        
        // 5. 用户维度限流规则（防止单个用户过度请求）
        FlowRule userRule = new FlowRule();
        userRule.setResource("userSeckill");
        userRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        userRule.setCount(10); // 每个用户每秒最多10个请求
        userRule.setStrategy(RuleConstant.STRATEGY_DIRECT);
        userRule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT);
        rules.add(userRule);
        
        // 加载流量控制规则
        FlowRuleManager.loadRules(rules);
        System.out.println("✅ Sentinel流量控制规则加载完成");
    }
    
    /**
     * 初始化熔断降级规则
     */
    private void initDegradeRules() {
        List<DegradeRule> rules = new ArrayList<>();
        
        // 1. 优惠券秒杀接口熔断规则
        DegradeRule seckillDegradeRule = new DegradeRule();
        seckillDegradeRule.setResource("seckillCoupon");
        seckillDegradeRule.setGrade(CircuitBreakerStrategy.ERROR_RATIO.getType());
        seckillDegradeRule.setCount(0.5); // 异常比例阈值50%
        seckillDegradeRule.setTimeWindow(10); // 熔断时长10秒
        seckillDegradeRule.setMinRequestAmount(5); // 最小请求数
        seckillDegradeRule.setStatIntervalMs(10000); // 统计时长10秒
        rules.add(seckillDegradeRule);
        
        // 2. 优惠券查询接口熔断规则
        DegradeRule queryDegradeRule = new DegradeRule();
        queryDegradeRule.setResource("queryCoupon");
        queryDegradeRule.setGrade(CircuitBreakerStrategy.ERROR_RATIO.getType());
        queryDegradeRule.setCount(0.3); // 异常比例阈值30%
        queryDegradeRule.setTimeWindow(5); // 熔断时长5秒
        queryDegradeRule.setMinRequestAmount(10); // 最小请求数
        queryDegradeRule.setStatIntervalMs(10000); // 统计时长10秒
        rules.add(queryDegradeRule);
        
        // 3. 优惠券领取接口熔断规则
        DegradeRule claimDegradeRule = new DegradeRule();
        claimDegradeRule.setResource("claimCoupon");
        claimDegradeRule.setGrade(CircuitBreakerStrategy.ERROR_RATIO.getType());
        claimDegradeRule.setCount(0.4); // 异常比例阈值40%
        claimDegradeRule.setTimeWindow(8); // 熔断时长8秒
        claimDegradeRule.setMinRequestAmount(3); // 最小请求数
        claimDegradeRule.setStatIntervalMs(10000); // 统计时长10秒
        rules.add(claimDegradeRule);
        
        // 加载熔断降级规则
        DegradeRuleManager.loadRules(rules);
        System.out.println("✅ Sentinel熔断降级规则加载完成");
    }
}
