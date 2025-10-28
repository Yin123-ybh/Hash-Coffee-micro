package com.coffee.userservice.service;

import com.coffee.userservice.entity.User;
import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户注册
     */
    User register(User user);
    
    /**
     * 用户登录
     */
    String login(String username, String password);
    
    /**
     * 微信登录
     */
    User wxLogin(String code, String nickname, String avatar, Integer gender, 
                 String city, String province, String country);
    
    /**
     * 根据ID查询用户
     */
    User getUserById(Long id);
    
    /**
     * 根据用户名查询用户
     */
    User getUserByUsername(String username);
    
    /**
     * 查询用户列表
     */
    List<User> getUserList(String username, String phone, Integer status);
    
    /**
     * 更新用户信息
     */
    boolean updateUser(User user);
    
    /**
     * 删除用户
     */
    boolean deleteUser(Long id);
    
    /**
     * 更新用户积分
     */
    boolean updateUserPoints(Long userId, Integer points);
    
    /**
     * 更新用户会员等级
     */
    boolean updateUserMemberLevel(Long userId, Integer memberLevel);
    
    /**
     * 验证用户是否存在
     */
    boolean checkUserExists(String username, String phone, String email);
}
