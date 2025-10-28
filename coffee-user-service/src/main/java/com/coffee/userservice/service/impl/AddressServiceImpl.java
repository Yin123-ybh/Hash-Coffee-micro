package com.coffee.userservice.service.impl;

import com.coffee.userservice.entity.Address;
import com.coffee.userservice.mapper.AddressMapper;
import com.coffee.userservice.service.AddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 地址服务实现类
 */
@Slf4j
@Service
public class AddressServiceImpl implements AddressService {
    
    @Autowired
    private AddressMapper addressMapper;
    
    @Override
    public List<Address> getAddressList(Long userId) {
        log.info("查询用户地址列表: userId={}", userId);
        return addressMapper.selectByUserId(userId);
    }
    
    @Override
    public Address getAddressById(Long id) {
        log.info("查询地址详情: id={}", id);
        return addressMapper.selectById(id);
    }
    
    @Override
    public Address getDefaultAddress(Long userId) {
        log.info("查询默认地址: userId={}", userId);
        return addressMapper.selectDefaultByUserId(userId);
    }
    
    @Override
    public boolean addAddress(Address address) {
        log.info("添加地址: userId={}, receiverName={}", address.getUserId(), address.getReceiverName());
        
        // 设置创建时间和更新时间
        address.setCreateTime(LocalDateTime.now());
        address.setUpdateTime(LocalDateTime.now());
        
        // 如果设置为默认地址，需要先取消其他默认地址
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            addressMapper.cancelDefaultByUserId(address.getUserId());
        }
        
        return addressMapper.insert(address) > 0;
    }
    
    @Override
    public boolean updateAddress(Address address) {
        log.info("更新地址: id={}", address.getId());
        
        // 设置更新时间
        address.setUpdateTime(LocalDateTime.now());
        
        // 如果设置为默认地址，需要先取消其他默认地址
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            addressMapper.cancelDefaultByUserId(address.getUserId());
        }
        
        return addressMapper.update(address) > 0;
    }
    
    @Override
    public boolean deleteAddress(Long id) {
        log.info("删除地址: id={}", id);
        return addressMapper.deleteById(id) > 0;
    }
    
    @Override
    public boolean setDefaultAddress(Long id, Long userId) {
        log.info("设置默认地址: id={}, userId={}", id, userId);
        
        // 先取消所有默认地址
        addressMapper.cancelDefaultByUserId(userId);
        
        // 设置新的默认地址
        return addressMapper.setDefault(id, userId) > 0;
    }
}









