package com.coffee.userservice.service.impl;

import com.coffee.common.utils.JwtUtils;
import com.coffee.userservice.entity.User;
import com.coffee.userservice.mapper.UserMapper;
import com.coffee.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public User register(User user) {
        // 检查用户是否已存在
        if (checkUserExists(user.getUsername(), user.getPhone(), user.getEmail())) {
            throw new RuntimeException("用户已存在");
        }
        
        // 密码加密
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        
        // 设置默认值
        user.setPoints(0);
        user.setMemberLevel(0);
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        userMapper.insert(user);
        log.info("用户注册成功: {}", user.getUsername());
        return user;
    }
    
    @Override
    public String login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        if (user.getStatus() == 0) {
            throw new RuntimeException("用户已被禁用");
        }
        
        String encryptedPassword = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!encryptedPassword.equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        // 生成JWT令牌
        String token = JwtUtils.generateToken(user.getId().toString(), user.getUsername());
        log.info("用户登录成功: {}", username);
        return token;
    }
    
    @Override
    public User wxLogin(String code, String nickname, String avatar, Integer gender, 
                       String city, String province, String country) {
        log.info("微信登录开始: code={}, nickname={}", code, nickname);
        
        // TODO: 实际项目中需要使用code调用微信接口获取openid
        // 这里简化处理：使用code模拟openid（生产环境需要调用微信API）
        String openid = "mock_openid_" + code;
        
        log.info("模拟获取openid: {}", openid);
        
        // 查找是否已存在用户（基于openid）
        User existingUser = userMapper.selectByOpenid(openid);
        
        if (existingUser != null) {
            // 用户已存在，更新信息
            log.info("用户已存在，更新用户信息: userId={}, nickname={}", existingUser.getId(), nickname);
            existingUser.setNickname(nickname);
            existingUser.setAvatar(avatar);
            existingUser.setGender(gender);
            existingUser.setCity(city);
            existingUser.setProvince(province);
            existingUser.setCountry(country);
            existingUser.setUpdateTime(LocalDateTime.now());
            userMapper.update(existingUser);
            return existingUser;
        }
        
        // 如果没有找到openid对应的用户，检查是否存在openid为空的同名用户
        // 这可能是历史数据迁移问题
        String tempUsername = "wx_user_" + openid.substring(0, Math.min(openid.length(), 8));
        User existingByUsername = userMapper.selectByUsername(tempUsername);
        if (existingByUsername != null && (existingByUsername.getOpenid() == null || existingByUsername.getOpenid().isEmpty())) {
            log.info("发现同名用户但openid为空，更新openid: userId={}, username={}", existingByUsername.getId(), tempUsername);
            existingByUsername.setOpenid(openid);
            existingByUsername.setNickname(nickname);
            existingByUsername.setAvatar(avatar);
            existingByUsername.setGender(gender);
            existingByUsername.setCity(city);
            existingByUsername.setProvince(province);
            existingByUsername.setCountry(country);
            existingByUsername.setUpdateTime(LocalDateTime.now());
            userMapper.update(existingByUsername);
            return existingByUsername;
        }
        
        // 创建新用户
        {
            // 使用时间戳确保用户名唯一性
            String username = "wx_user_" + System.currentTimeMillis() + "_" + 
                             (code != null && code.length() > 3 ? code.substring(0, Math.min(code.length(), 4)) : "user");
            log.info("创建新用户: username={}, nickname={}", username, nickname);
            
            // 确保用户名唯一，如果冲突则重试
            int maxRetries = 3;
            int retryCount = 0;
            User user = null;
            
            while (retryCount < maxRetries) {
                try {
                    user = new User();
                    user.setUsername(username);
                    user.setPhone("");  // 微信登录时手机号为空
                    user.setPassword("");  // 微信登录时密码为空
                    user.setOpenid(openid);
                    user.setNickname(nickname);
                    user.setAvatar(avatar);
                    user.setGender(gender);
                    user.setCity(city);
                    user.setProvince(province);
                    user.setCountry(country);
                    user.setPoints(0);
                    user.setMemberLevel(0);
                    user.setStatus(1);
                    user.setCreateTime(LocalDateTime.now());
                    user.setUpdateTime(LocalDateTime.now());
                    
                    userMapper.insert(user);
                    log.info("新用户创建成功: userId={}, username={}", user.getId(), username);
                    return user;
                    
                } catch (Exception e) {
                    retryCount++;
                    log.warn("创建用户失败（重试 {}/{}）: {}", retryCount, maxRetries, e.getMessage());
                    
                    if (retryCount >= maxRetries) {
                        log.error("创建用户失败，已达到最大重试次数");
                        throw new RuntimeException("创建用户失败: " + e.getMessage());
                    }
                    
                    // 生成新的用户名
                    username = "wx_user_" + System.currentTimeMillis() + "_" + retryCount;
                    log.info("重试创建用户，新用户名: {}", username);
                }
            }
            
            return user;
        }
    }
    
    @Override
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }
    
    @Override
    public User getUserByUsername(String username) {
        return userMapper.selectByUsername(username);
    }
    
    @Override
    public List<User> getUserList(String username, String phone, Integer status) {
        try {
            // 简化实现：如果所有参数都是null，则查询所有用户
            if (username == null && phone == null && status == null) {
                log.info("查询所有用户");
                return userMapper.selectAll();
            } else {
                log.info("查询用户列表: username={}, phone={}, status={}", username, phone, status);
                return userMapper.selectList(username, phone, status);
            }
        } catch (Exception e) {
            log.error("查询用户列表失败: {}", e.getMessage(), e);
            // 返回空列表而不是抛出异常
            return new ArrayList<>();
        }
    }
    
    @Override
    public boolean updateUser(User user) {
        user.setUpdateTime(LocalDateTime.now());
        return userMapper.update(user) > 0;
    }
    
    @Override
    public boolean deleteUser(Long id) {
        return userMapper.deleteById(id) > 0;
    }
    
    @Override
    public boolean updateUserPoints(Long userId, Integer points) {
        return userMapper.updatePoints(userId, points) > 0;
    }
    
    @Override
    public boolean updateUserMemberLevel(Long userId, Integer memberLevel) {
        return userMapper.updateMemberLevel(userId, memberLevel) > 0;
    }
    
    @Override
    public boolean checkUserExists(String username, String phone, String email) {
        User user = userMapper.selectByUsername(username);
        if (user != null) {
            return true;
        }
        
        user = userMapper.selectByPhone(phone);
        if (user != null) {
            return true;
        }
        
        user = userMapper.selectByEmail(email);
        return user != null;
    }
}
