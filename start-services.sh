#!/bin/bash

# 咖啡点餐系统微服务启动脚本

echo "☕️ 启动咖啡点餐系统微服务..."

# 检查Java环境
if ! command -v java &> /dev/null; then
    echo "❌ 错误: 未找到Java环境，请先安装JDK 8+"
    exit 1
fi

# 检查Maven环境
if ! command -v mvn &> /dev/null; then
    echo "❌ 错误: 未找到Maven环境，请先安装Maven 3.6+"
    exit 1
fi

# 检查Nacos是否启动
echo "🔍 检查Nacos服务状态..."
if ! curl -s http://localhost:8848/nacos/ > /dev/null; then
    echo "❌ 错误: Nacos服务未启动，请先启动Nacos"
    echo "启动命令: sh nacos/bin/startup.sh -m standalone"
    exit 1
fi

echo "✅ Nacos服务正常运行"

# 编译项目
echo "🔨 编译项目..."
mvn clean install -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ 编译失败"
    exit 1
fi

echo "✅ 编译成功"

# 启动服务函数
start_service() {
    local service_name=$1
    local service_dir=$2
    local port=$3
    
    echo "🚀 启动 $service_name 服务 (端口: $port)..."
    
    cd $service_dir
    
    # 检查端口是否被占用
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null; then
        echo "⚠️  端口 $port 已被占用，跳过启动 $service_name"
        cd ..
        return
    fi
    
    # 启动服务
    nohup mvn spring-boot:run > ../logs/$service_name.log 2>&1 &
    local pid=$!
    echo $pid > ../logs/$service_name.pid
    
    echo "✅ $service_name 服务启动成功 (PID: $pid)"
    cd ..
}

# 创建日志目录
mkdir -p logs

# 启动各个微服务
start_service "coffee-gateway" "coffee-gateway" "8089"
sleep 5

start_service "coffee-user-service" "coffee-user-service" "8081"
sleep 3

start_service "coffee-product-service" "coffee-product-service" "8082"
sleep 3

start_service "coffee-order-service" "coffee-order-service" "8083"
sleep 3

start_service "coffee-coupon-service" "coffee-coupon-service" "8084"
sleep 3

start_service "coffee-cart-service" "coffee-cart-service" "8085"
sleep 3

start_service "coffee-ai-service" "coffee-ai-service" "8086"
sleep 3

start_service "coffee-statistics-service" "coffee-statistics-service" "8087"

echo ""
echo "🎉 所有微服务启动完成！"
echo ""
echo "📊 服务状态:"
echo "- API网关: http://localhost:8089"
echo "- 用户服务: http://localhost:8081"
echo "- 商品服务: http://localhost:8082"
echo "- 订单服务: http://localhost:8083"
echo "- 优惠券服务: http://localhost:8084"
echo "- 购物车服务: http://localhost:8085"
echo "- AI服务: http://localhost:8086"
echo "- 统计服务: http://localhost:8087"
echo ""
echo "📋 管理界面:"
echo "- Nacos控制台: http://localhost:8848/nacos"
echo "- API文档: http://localhost:8081/doc.html (用户服务)"
echo ""
echo "📝 日志文件位置: logs/"
echo "🛑 停止服务: ./stop-services.sh"
