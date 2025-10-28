package com.coffee.userservice.controller;

import com.coffee.common.result.Result;
import com.coffee.common.utils.JwtUtils;
import com.coffee.userservice.entity.User;
import com.coffee.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) {
        try {
            User registeredUser = userService.register(user);
            return Result.success("注册成功", registeredUser);
        } catch (Exception e) {
            log.error("用户注册失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 传统登录（用户名密码）
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestParam(required = false) String username, 
                                              @RequestParam(required = false) String password,
                                              @RequestBody(required = false) Map<String, Object> wxLoginData) {
        try {
            // 处理微信小程序登录
            if (wxLoginData != null && wxLoginData.containsKey("code")) {
                return handleWxLogin(wxLoginData);
            }
            
            // 处理传统登录
            if (username != null && password != null) {
                String token = userService.login(username, password);
                Map<String, Object> result = new HashMap<>();
                result.put("token", token);
                return Result.success("登录成功", result);
            }
            
            return Result.error("登录参数错误");
        } catch (Exception e) {
            log.error("用户登录失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 处理微信登录
     */
    private Result<Map<String, Object>> handleWxLogin(Map<String, Object> wxLoginData) {
        try {
            String code = (String) wxLoginData.get("code");
            String nickname = (String) wxLoginData.get("nickname");
            String avatar = (String) wxLoginData.get("avatar");
            Integer gender = wxLoginData.get("gender") != null ? 
                Integer.valueOf(wxLoginData.get("gender").toString()) : 0;
            String city = (String) wxLoginData.get("city");
            String province = (String) wxLoginData.get("province");
            String country = (String) wxLoginData.get("country");
            
            log.info("微信登录请求: code={}, nickname={}", code, nickname);
            
            // 创建或更新用户
            User user = userService.wxLogin(code, nickname, avatar, gender, city, province, country);
            
            // 生成JWT token
            String token = JwtUtils.generateToken(user.getId().toString(), user.getUsername());
            
            // 返回用户信息和token
            Map<String, Object> result = new HashMap<>();
            result.put("id", user.getId());
            result.put("username", user.getUsername());
            result.put("nickname", user.getNickname());
            result.put("avatar", user.getAvatar());
            result.put("memberLevel", user.getMemberLevel());
            result.put("points", user.getPoints());
            result.put("token", token);
            
            log.info("微信登录成功: userId={}, nickname={}", user.getId(), user.getNickname());
            return Result.success("登录成功", result);
        } catch (Exception e) {
            log.error("微信登录失败: {}", e.getMessage(), e);
            return Result.error("登录失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/info")
    public Result<User> getUserInfo(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                return Result.unauthorized("未授权访问");
            }
            
            token = token.substring(7);
            String userId = JwtUtils.getUserIdFromToken(token);
            if (userId == null) {
                return Result.unauthorized("令牌无效");
            }
            
            User user = userService.getUserById(Long.valueOf(userId));
            if (user == null) {
                return Result.notFound("用户不存在");
            }
            
            // 清除敏感信息
            user.setPassword(null);
            return Result.success(user);
        } catch (Exception e) {
            log.error("获取用户信息失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/list")
    public Result<Map<String, Object>> getUserList(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        try {
            log.info("接收用户列表请求: username={}, phone={}, status={}, page={}, pageSize={}", 
                    username, phone, status, page, pageSize);
            
            // 调用服务获取用户列表
            List<User> users = userService.getUserList(username, phone, status);
            
            // 构建返回数据
            Map<String, Object> result = new HashMap<>();
            result.put("records", users);
            result.put("total", users.size());
            
            log.info("返回用户列表: 共{}条", users.size());
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询用户列表失败: {}", e.getMessage(), e);
            
            // 如果查询失败，返回空列表而不是错误
            Map<String, Object> result = new HashMap<>();
            result.put("records", new ArrayList<>());
            result.put("total", 0);
            
            return Result.success(result);
        }
    }
    
    @PutMapping("/update")
    public Result<Boolean> updateUser(@RequestBody User user) {
        try {
            boolean success = userService.updateUser(user);
            return success ? Result.success("更新成功", true) : Result.error("更新失败");
        } catch (Exception e) {
            log.error("更新用户信息失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> deleteUser(@PathVariable Long id) {
        try {
            boolean success = userService.deleteUser(id);
            return success ? Result.success("删除成功", true) : Result.error("删除失败");
        } catch (Exception e) {
            log.error("删除用户失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    @PutMapping("/points")
    public Result<Boolean> updateUserPoints(@RequestParam Long userId, @RequestParam Integer points) {
        try {
            boolean success = userService.updateUserPoints(userId, points);
            return success ? Result.success("积分更新成功", true) : Result.error("积分更新失败");
        } catch (Exception e) {
            log.error("更新用户积分失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    @PutMapping("/member-level")
    public Result<Boolean> updateUserMemberLevel(@RequestParam Long userId, @RequestParam Integer memberLevel) {
        try {
            boolean success = userService.updateUserMemberLevel(userId, memberLevel);
            return success ? Result.success("会员等级更新成功", true) : Result.error("会员等级更新失败");
        } catch (Exception e) {
            log.error("更新用户会员等级失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
}
