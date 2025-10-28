package com.coffee.userservice.util;

import javax.servlet.http.HttpServletRequest;

/**
 * Controller工具类
 * 网关已经验证了JWT并传递用户信息到请求头
 */
public class ControllerUtil {
    
    /**
     * 从请求头中获取用户ID（网关已验证JWT并添加到请求头）
     * @param request HTTP请求
     * @return 用户ID，如果获取失败返回null
     */
    public static Long getUserId(HttpServletRequest request) {
        try {
            String userId = request.getHeader("X-User-Id");
            return userId != null ? Long.valueOf(userId) : null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 从请求头中获取用户名（网关已验证JWT并添加到请求头）
     * @param request HTTP请求
     * @return 用户名，如果获取失败返回null
     */
    public static String getUsername(HttpServletRequest request) {
        try {
            return request.getHeader("X-User-Name");
        } catch (Exception e) {
            return null;
        }
    }
}
