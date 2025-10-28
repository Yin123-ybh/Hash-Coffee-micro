package com.coffee.userservice.service;

import com.coffee.userservice.entity.Address;
import java.util.List;

/**
 * 地址服务接口
 */
public interface AddressService {
    
    /**
     * 获取用户地址列表
     */
    List<Address> getAddressList(Long userId);
    
    /**
     * 根据ID获取地址详情
     */
    Address getAddressById(Long id);
    
    /**
     * 获取默认地址
     */
    Address getDefaultAddress(Long userId);
    
    /**
     * 添加地址
     */
    boolean addAddress(Address address);
    
    /**
     * 更新地址
     */
    boolean updateAddress(Address address);
    
    /**
     * 删除地址
     */
    boolean deleteAddress(Long id);
    
    /**
     * 设置默认地址
     */
    boolean setDefaultAddress(Long id, Long userId);
}









