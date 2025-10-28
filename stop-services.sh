#!/bin/bash

# 咖啡点餐系统微服务停止脚本

echo "🛑 停止咖啡点餐系统微服务..."

# 停止服务函数
stop_service() {
    local service_name=$1
    local pid_file="logs/$service_name.pid"
    
    if [ -f "$pid_file" ]; then
        local pid=$(cat $pid_file)
        if ps -p $pid > /dev/null; then
            echo "🛑 停止 $service_name 服务 (PID: $pid)..."
            kill $pid
            sleep 2
            
            # 强制杀死进程（如果还在运行）
            if ps -p $pid > /dev/null; then
                echo "⚠️  强制停止 $service_name 服务..."
                kill -9 $pid
            fi
            
            echo "✅ $service_name 服务已停止"
        else
            echo "⚠️  $service_name 服务未运行"
        fi
        rm -f $pid_file
    else
        echo "⚠️  未找到 $service_name 服务的PID文件"
    fi
}

# 停止所有微服务
stop_service "coffee-statistics-service"
stop_service "coffee-ai-service"
stop_service "coffee-cart-service"
stop_service "coffee-coupon-service"
stop_service "coffee-order-service"
stop_service "coffee-product-service"
stop_service "coffee-user-service"
stop_service "coffee-gateway"

# 清理端口占用
echo "🧹 清理端口占用..."
for port in 8089 8081 8082 8083 8084 8085 8086 8087; do
    pid=$(lsof -ti:$port)
    if [ ! -z "$pid" ]; then
        echo "🛑 清理端口 $port (PID: $pid)"
        kill -9 $pid 2>/dev/null
    fi
done

echo ""
echo "✅ 所有微服务已停止"
echo "📝 日志文件保留在 logs/ 目录中"
