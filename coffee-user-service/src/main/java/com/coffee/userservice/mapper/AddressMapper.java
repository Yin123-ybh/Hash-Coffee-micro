package com.coffee.userservice.mapper;

import com.coffee.userservice.entity.Address;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 地址Mapper接口
 */
@Mapper
public interface AddressMapper {
    
    /**
     * 根据ID查询地址
     */
    Address selectById(@Param("id") Long id);
    
    /**
     * 根据用户ID查询地址列表
     */
    List<Address> selectByUserId(@Param("userId") Long userId);
    
    /**
     * 根据用户ID查询默认地址
     */
    Address selectDefaultByUserId(@Param("userId") Long userId);
    
    /**
     * 插入地址
     */
    int insert(Address address);
    
    /**
     * 更新地址
     */
    int update(Address address);
    
    /**
     * 根据ID删除地址
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 取消用户所有默认地址
     */
    int cancelDefaultByUserId(@Param("userId") Long userId);
    
    /**
     * 设置默认地址
     */
    int setDefault(@Param("id") Long id, @Param("userId") Long userId);
}









