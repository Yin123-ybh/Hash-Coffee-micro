package com.coffee.orderservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置类
 * 实现订单异步处理和超时取消
 */
@Configuration
public class RabbitMQConfig {
    
    // 订单相关队列和交换机
    public static final String ORDER_CREATE_QUEUE = "order.create.queue";
    public static final String ORDER_PAY_QUEUE = "order.pay.queue";
    public static final String ORDER_CANCEL_QUEUE = "order.cancel.queue";
    public static final String ORDER_TIMEOUT_QUEUE = "order.timeout.queue";
    public static final String ORDER_DLQ = "order.dlq";
    
    // 交换机
    public static final String ORDER_EXCHANGE = "order.exchange";
    public static final String COURSE_DELAY_EXCHANGE = "order.delay.exchange";
    
    // 路由键
    public static final String ORDER_CREATE_ROUTING_KEY = "order.create";
    public static final String ORDER_PAY_ROUTING_KEY = "order.pay";
    public static final String ORDER_CANCEL_ROUTING_KEY = "order.cancel";
    public static final String ORDER_TIMEOUT_ROUTING_KEY = "order.timeout";
    
    /**
     * 订单创建队列
     */
    @Bean
    public Queue orderCreateQueue() {
        return QueueBuilder.durable(ORDER_CREATE_QUEUE)
                .withArgument("x-dead-letter-exchange", ORDER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", ORDER_DLQ)
                .build();
    }
    
    /**
     * 订单支付队列
     */
    @Bean
    public Queue orderPayQueue() {
        return QueueBuilder.durable(ORDER_PAY_QUEUE)
                .withArgument("x-dead-letter-exchange", ORDER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", ORDER_DLQ)
                .build();
    }
    
    /**
     * 订单取消队列
     */
    @Bean
    public Queue orderCancelQueue() {
        return QueueBuilder.durable(ORDER_CANCEL_QUEUE)
                .withArgument("x-dead-letter-exchange", ORDER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", ORDER_DLQ)
                .build();
    }
    
    /**
     * 订单超时队列（延迟队列）
     */
    @Bean
    public Queue orderTimeoutQueue() {
        return QueueBuilder.durable(ORDER_TIMEOUT_QUEUE)
                .withArgument("x-dead-letter-exchange", ORDER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", ORDER_CANCEL_ROUTING_KEY)
                .build();
    }
    
    /**
     * 死信队列
     */
    @Bean
    public Queue orderDlq() {
        return QueueBuilder.durable(ORDER_DLQ).build();
    }
    
    /**
     * 订单交换机
     */
    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(ORDER_EXCHANGE);
    }
    
    /**
     * 延迟交换机
     */
    @Bean
    public TopicExchange orderDelayExchange() {
        return new TopicExchange(COURSE_DELAY_EXCHANGE);
    }
    
    /**
     * 绑定订单创建队列到交换机
     */
    @Bean
    public Binding orderCreateBinding() {
        return BindingBuilder.bind(orderCreateQueue())
                .to(orderExchange())
                .with(ORDER_CREATE_ROUTING_KEY);
    }
    
    /**
     * 绑定订单支付队列到交换机
     */
    @Bean
    public Binding orderPayBinding() {
        return BindingBuilder.bind(orderPayQueue())
                .to(orderExchange())
                .with(ORDER_PAY_ROUTING_KEY);
    }
    
    /**
     * 绑定订单取消队列到交换机
     */
    @Bean
    public Binding orderCancelBinding() {
        return BindingBuilder.bind(orderCancelQueue())
                .to(orderExchange())
                .with(ORDER_CANCEL_ROUTING_KEY);
    }
    
    /**
     * 绑定订单超时队列到延迟交换机
     */
    @Bean
    public Binding orderTimeoutBinding() {
        return BindingBuilder.bind(orderTimeoutQueue())
                .to(orderDelayExchange())
                .with(ORDER_TIMEOUT_ROUTING_KEY);
    }
    
    /**
     * 绑定死信队列到交换机
     */
    @Bean
    public Binding orderDlqBinding() {
        return BindingBuilder.bind(orderDlq())
                .to(orderExchange())
                .with(ORDER_DLQ);
    }
    
    /**
     * RabbitTemplate配置
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        
        // 设置确认回调
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                System.out.println("消息发送成功: " + correlationData);
            } else {
                System.out.println("消息发送失败: " + cause);
            }
        });
        
        // 设置返回回调
        rabbitTemplate.setReturnsCallback(returned -> {
            System.out.println("消息被退回: " + returned.getMessage());
            System.out.println("退回原因: " + returned.getReplyText());
        });
        
        return rabbitTemplate;
    }
    
    /**
     * 消息监听器容器工厂配置
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        
        // 设置并发消费者数量
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        
        // 设置预取数量
        factory.setPrefetchCount(1);
        
        // 设置手动确认
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        
        return factory;
    }
}
