package com.coffee.userservice.controller;

import com.coffee.common.result.Result;
import com.coffee.userservice.entity.Address;
import com.coffee.userservice.service.AddressService;
import com.coffee.userservice.util.ControllerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 地址控制器
 */
@Slf4j
@RestController
@RequestMapping("/address")
public class AddressController {
    
    @Autowired
    private AddressService addressService;
    
    /**
     * 获取地址列表
     */
    @GetMapping("/list")
    public Result<List<Address>> getAddressList(HttpServletRequest request) {
        try {
            Long userId = ControllerUtil.getUserId(request);
            if (userId == null) {
                return Result.unauthorized("未授权访问");
            }
            
            List<Address> addresses = addressService.getAddressList(userId);
            return Result.success(addresses);
        } catch (Exception e) {
            log.error("获取地址列表失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取地址详情
     */
    @GetMapping("/{id}")
    public Result<Address> getAddressDetail(@PathVariable Long id) {
        try {
            Address address = addressService.getAddressById(id);
            if (address == null) {
                return Result.notFound("地址不存在");
            }
            return Result.success(address);
        } catch (Exception e) {
            log.error("获取地址详情失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取默认地址
     */
    @GetMapping("/default")
    public Result<Address> getDefaultAddress(HttpServletRequest request) {
        try {
            Long userId = ControllerUtil.getUserId(request);
            if (userId == null) {
                return Result.unauthorized("未授权访问");
            }
            
            Address address = addressService.getDefaultAddress(userId);
            return Result.success(address);
        } catch (Exception e) {
            log.error("获取默认地址失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 添加地址
     */
    @PostMapping
    public Result<Boolean> addAddress(@RequestBody Address address, HttpServletRequest request) {
        try {
            Long userId = ControllerUtil.getUserId(request);
            if (userId == null) {
                return Result.unauthorized("未授权访问");
            }
            
            address.setUserId(userId);
            boolean success = addressService.addAddress(address);
            return success ? Result.success("添加成功", true) : Result.error("添加失败");
        } catch (Exception e) {
            log.error("添加地址失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新地址
     */
    @PutMapping
    public Result<Boolean> updateAddress(@RequestBody Address address) {
        try {
            boolean success = addressService.updateAddress(address);
            return success ? Result.success("更新成功", true) : Result.error("更新失败");
        } catch (Exception e) {
            log.error("更新地址失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除地址
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteAddress(@PathVariable Long id) {
        try {
            boolean success = addressService.deleteAddress(id);
            return success ? Result.success("删除成功", true) : Result.error("删除失败");
        } catch (Exception e) {
            log.error("删除地址失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 设置默认地址
     */
    @PutMapping("/{id}/default")
    public Result<Boolean> setDefaultAddress(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long userId = ControllerUtil.getUserId(request);
            if (userId == null) {
                return Result.unauthorized("未授权访问");
            }
            
            boolean success = addressService.setDefaultAddress(id, userId);
            return success ? Result.success("设置成功", true) : Result.error("设置失败");
        } catch (Exception e) {
            log.error("设置默认地址失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
}
