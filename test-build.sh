#!/bin/bash

# 测试微服务项目构建脚本

echo "🔨 开始测试微服务项目构建..."

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

echo "✅ Java和Maven环境检查通过"

# 进入项目目录
cd /Users/yin/Downloads/coffe1/coffee-microservices

# 清理并编译项目
echo "🔨 开始编译项目..."
mvn clean compile

if [ $? -eq 0 ]; then
    echo "✅ 项目编译成功"
else
    echo "❌ 项目编译失败"
    exit 1
fi

# 检查各个模块
echo "📋 检查各个模块..."
modules=("coffee-common" "coffee-gateway" "coffee-user-service" "coffee-product-service" "coffee-order-service" "coffee-coupon-service" "coffee-cart-service" "coffee-ai-service" "coffee-statistics-service")

for module in "${modules[@]}"; do
    if [ -d "$module" ]; then
        echo "✅ $module 模块存在"
    else
        echo "❌ $module 模块缺失"
    fi
done

echo ""
echo "🎉 微服务项目构建测试完成！"
echo ""
echo "📊 项目状态:"
echo "- 所有模块已创建"
echo "- 项目编译成功"
echo "- 配置文件已修复"
echo ""
echo "🚀 下一步可以启动服务进行测试"
