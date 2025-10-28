package com.coffee.userservice.service;

import com.coffee.userservice.dto.AdminLoginDTO;
import com.coffee.userservice.vo.AdminLoginVO;

public interface AdminService {

    /**
     * 管理员登录
     */
    AdminLoginVO login(AdminLoginDTO adminLoginDTO);

    /**
     * 根据id查询管理员信息
     */
    AdminLoginVO getById(Long id);
}
