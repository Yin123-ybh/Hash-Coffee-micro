-- 创建秒杀活动表
CREATE TABLE IF NOT EXISTS coupon_seckill_activity (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '活动名称',
    description TEXT COMMENT '活动描述',
    coupon_id BIGINT NOT NULL COMMENT '优惠券ID',
    seckill_stock INT NOT NULL COMMENT '秒杀库存',
    per_user_limit INT DEFAULT 1 COMMENT '每人限领数量',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    status TINYINT DEFAULT 0 COMMENT '状态:0-未开始,1-进行中,2-已结束,3-已取消',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_coupon_id (coupon_id),
    INDEX idx_status (status),
    INDEX idx_start_time (start_time),
    INDEX idx_end_time (end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券秒杀活动表';

-- 创建秒杀参与记录表
CREATE TABLE IF NOT EXISTS coupon_seckill_participant (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    activity_id BIGINT NOT NULL COMMENT '活动ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    quantity INT NOT NULL COMMENT '参与数量',
    status TINYINT DEFAULT 0 COMMENT '状态:0-待发放,1-已发放,2-已取消',
    user_coupon_id BIGINT COMMENT '用户优惠券ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '参与时间',
    INDEX idx_activity_id (activity_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='秒杀参与记录表';


