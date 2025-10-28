-- 管理员表
-- 需要在 coffee_user_db 数据库中执行

USE coffee_user_db;

CREATE TABLE IF NOT EXISTS admin (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(32) UNIQUE NOT NULL COMMENT '用户名',
    password VARCHAR(64) NOT NULL COMMENT '密码(MD5加密)',
    name VARCHAR(64) COMMENT '姓名',
    phone VARCHAR(11) COMMENT '手机号',
    sex VARCHAR(2) COMMENT '性别',
    id_number VARCHAR(18) COMMENT '身份证号',
    avatar VARCHAR(255) COMMENT '头像',
    status TINYINT DEFAULT 1 COMMENT '状态 0:禁用 1:正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- 插入默认管理员账号
-- 用户名: admin
-- 密码: 123456 (MD5: e10adc3949ba59abbe56e057f20f883e)
INSERT INTO admin (username, password, name, status) VALUES 
('admin', 'e10adc3949ba59abbe56e057f20f883e', '管理员', 1)
ON DUPLICATE KEY UPDATE username=username;
