package com.coffee.couponservice.config;

import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Sentinel流量控制配置
 */
@Configuration
public class SentinelConfig {
    
    /**
     * 配置Sentinel资源切面
     */
    @Bean
    public SentinelResourceAspect sentinelResourceAspect() {
        return new SentinelResourceAspect();
    }
    
    /**
     * 配置RestTemplate
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    /**
     * 初始化流量控制规则
     */
    @PostConstruct
    public void initFlowRules() {
        List<FlowRule> rules = new ArrayList<>();
        
        // 优惠券秒杀接口限流规则
        FlowRule seckillRule = new FlowRule();
        seckillRule.setResource("seckillCoupon");
        seckillRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        seckillRule.setCount(100); // 每秒允许100个请求
        seckillRule.setStrategy(RuleConstant.STRATEGY_DIRECT);
        seckillRule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT);
        rules.add(seckillRule);
        
        // 优惠券查询接口限流规则
        FlowRule queryRule = new FlowRule();
        queryRule.setResource("queryCoupon");
        queryRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        queryRule.setCount(200); // 每秒允许200个请求
        queryRule.setStrategy(RuleConstant.STRATEGY_DIRECT);
        queryRule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT);
        rules.add(queryRule);
        
        // 用户维度限流规则
        FlowRule userRule = new FlowRule();
        userRule.setResource("userSeckill");
        userRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        userRule.setCount(10); // 每个用户每秒最多10个请求
        userRule.setStrategy(RuleConstant.STRATEGY_DIRECT);
        userRule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT);
        rules.add(userRule);
        
        // 加载规则
        FlowRuleManager.loadRules(rules);
    }
}
