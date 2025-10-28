/**
 * 微服务API配置
 * 前端调用微服务的配置文件
 */

// 微服务API地址配置
const MICROSERVICES_CONFIG = {
  // 开发环境
  development: {
    gateway: 'http://localhost:8080',
    userService: 'http://localhost:8081',
    productService: 'http://localhost:8082',
    orderService: 'http://localhost:8083',
    couponService: 'http://localhost:8084',
    cartService: 'http://localhost:8085',
    aiService: 'http://localhost:8086',
    statisticsService: 'http://localhost:8087'
  },
  
  // 测试环境
  testing: {
    gateway: 'http://test-gateway.coffee.com',
    userService: 'http://test-user.coffee.com',
    productService: 'http://test-product.coffee.com',
    orderService: 'http://test-order.coffee.com',
    couponService: 'http://test-coupon.coffee.com',
    cartService: 'http://test-cart.coffee.com',
    aiService: 'http://test-ai.coffee.com',
    statisticsService: 'http://test-statistics.coffee.com'
  },
  
  // 生产环境
  production: {
    gateway: 'https://api.coffee.com',
    userService: 'https://user.coffee.com',
    productService: 'https://product.coffee.com',
    orderService: 'https://order.coffee.com',
    couponService: 'https://coupon.coffee.com',
    cartService: 'https://cart.coffee.com',
    aiService: 'https://ai.coffee.com',
    statisticsService: 'https://statistics.coffee.com'
  }
};

// 当前环境
const currentEnv = process.env.NODE_ENV || 'development';

// 获取当前环境的配置
const config = MICROSERVICES_CONFIG[currentEnv];

// API接口配置
export const API_CONFIG = {
  // 基础配置
  baseURL: config.gateway,
  timeout: 10000,
  
  // 微服务API路径
  apis: {
    // 用户服务
    user: {
      login: '/user/login',
      register: '/user/register',
      info: '/user/info',
      list: '/user/list',
      update: '/user/update',
      delete: '/user/delete',
      points: '/user/points',
      memberLevel: '/user/member-level'
    },
    
    // 商品服务
    product: {
      list: '/product/list',
      detail: '/product/{id}',
      add: '/product/add',
      update: '/product/update',
      delete: '/product/delete',
      recommended: '/product/recommended',
      hot: '/product/hot',
      stock: '/product/stock'
    },
    
    // 订单服务
    order: {
      list: '/order/list',
      detail: '/order/{id}',
      create: '/order/create',
      update: '/order/update',
      cancel: '/order/cancel',
      pay: '/order/pay'
    },
    
    // 优惠券服务
    coupon: {
      list: '/coupon/list',
      detail: '/coupon/{id}',
      add: '/coupon/add',
      update: '/coupon/update',
      delete: '/coupon/delete',
      use: '/coupon/use',
      seckill: '/coupon/seckill'
    },
    
    // 购物车服务
    cart: {
      list: '/cart/list',
      add: '/cart/add',
      update: '/cart/update',
      delete: '/cart/delete',
      clear: '/cart/clear'
    },
    
    // AI服务
    ai: {
      chat: '/ai/chat',
      recommend: '/ai/recommend',
      analyze: '/ai/analyze'
    },
    
    // 统计服务
    statistics: {
      sales: '/statistics/sales',
      users: '/statistics/users',
      products: '/statistics/products',
      orders: '/statistics/orders'
    }
  }
};

// 微服务直接调用配置（绕过网关）
export const DIRECT_API_CONFIG = {
  userService: config.userService,
  productService: config.productService,
  orderService: config.orderService,
  couponService: config.couponService,
  cartService: config.cartService,
  aiService: config.aiService,
  statisticsService: config.statisticsService
};

// 请求拦截器配置
export const REQUEST_CONFIG = {
  // 请求头配置
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  },
  
  // 认证配置
  auth: {
    tokenKey: 'Authorization',
    tokenPrefix: 'Bearer '
  },
  
  // 错误处理配置
  error: {
    showMessage: true,
    logError: true
  }
};

export default API_CONFIG;
