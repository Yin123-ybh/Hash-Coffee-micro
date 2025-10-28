#!/bin/bash

# 微服务数据库创建脚本

echo "🗄️ 开始创建微服务数据库..."

# 数据库连接信息
DB_HOST="localhost"
DB_PORT="3306"
DB_USER="root"
DB_PASSWORD="MyNewPassword123!"

# 检查MySQL是否运行
echo "🔍 检查MySQL服务状态..."
if ! mysqladmin ping -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASSWORD --silent; then
    echo "❌ 错误: MySQL服务未运行，请先启动MySQL服务"
    exit 1
fi

echo "✅ MySQL服务正常运行"

# 创建数据库
echo "📊 创建微服务数据库..."

# 数据库列表
databases=(
    "coffee_user_db"
    "coffee_product_db" 
    "coffee_order_db"
    "coffee_coupon_db"
    "coffee_cart_db"
    "coffee_ai_db"
    "coffee_statistics_db"
)

# 创建数据库
for db in "${databases[@]}"; do
    echo "创建数据库: $db"
    mysql -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASSWORD -e "CREATE DATABASE IF NOT EXISTS $db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
    if [ $? -eq 0 ]; then
        echo "✅ 数据库 $db 创建成功"
    else
        echo "❌ 数据库 $db 创建失败"
    fi
done

echo ""
echo "🎉 所有微服务数据库创建完成！"
echo ""
echo "📋 已创建的数据库:"
for db in "${databases[@]}"; do
    echo "- $db"
done
echo ""
echo "📝 下一步: 运行数据库初始化脚本创建表结构"
echo "命令: mysql -u root -p < database-scripts/create-databases.sql"
