package com.coffee.userservice.mapper;

import com.coffee.userservice.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper {
    
    /**
     * 根据ID查询用户
     */
    User selectById(@Param("id") Long id);
    
    /**
     * 根据用户名查询用户
     */
    User selectByUsername(@Param("username") String username);
    
    /**
     * 根据手机号查询用户
     */
    User selectByPhone(@Param("phone") String phone);
    
    /**
     * 根据邮箱查询用户
     */
    User selectByEmail(@Param("email") String email);
    
    /**
     * 根据昵称查询用户
     */
    User selectByNickname(@Param("nickname") String nickname);
    
    /**
     * 根据openid查询用户
     */
    User selectByOpenid(@Param("openid") String openid);
    
    /**
     * 查询用户列表
     */
    List<User> selectList(@Param("username") String username, 
                         @Param("phone") String phone,
                         @Param("status") Integer status);
    
    /**
     * 查询所有用户
     */
    List<User> selectAll();
    
    /**
     * 插入用户
     */
    int insert(User user);
    
    /**
     * 更新用户
     */
    int update(User user);
    
    /**
     * 根据ID删除用户
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 更新用户积分
     */
    int updatePoints(@Param("id") Long id, @Param("points") Integer points);
    
    /**
     * 更新用户会员等级
     */
    int updateMemberLevel(@Param("id") Long id, @Param("memberLevel") Integer memberLevel);
}
