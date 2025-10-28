package com.coffee.userservice.service.impl;

import com.coffee.common.utils.JwtUtils;
import com.coffee.userservice.dto.AdminLoginDTO;
import com.coffee.userservice.entity.Admin;
import com.coffee.userservice.mapper.AdminMapper;
import com.coffee.userservice.service.AdminService;
import com.coffee.userservice.vo.AdminLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Slf4j
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public AdminLoginVO login(AdminLoginDTO adminLoginDTO) {
        String username = adminLoginDTO.getUsername();
        String password = adminLoginDTO.getPassword();
        
        log.info("开始管理员登录验证，用户名：{}", username);

        // 1、根据用户名查询数据库
        Admin admin = adminMapper.getByUsername(username);
        
        // 2、处理各种异常情况
        if (admin == null) {
            log.error("管理员账号不存在：{}", username);
            throw new RuntimeException("管理员账号不存在");
        }

        // 3、密码比对
        String encryptedPassword = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!encryptedPassword.equals(admin.getPassword())) {
            log.error("管理员密码错误，用户名：{}", username);
            throw new RuntimeException("密码错误");
        }

        // 4、判断账号状态
        if (admin.getStatus() == 0) {
            log.error("管理员账号被锁定，用户名：{}", username);
            throw new RuntimeException("账号已被禁用");
        }
        
        log.info("管理员验证通过，开始生成JWT Token");

        // 5、生成JWT令牌
        String token = JwtUtils.generateToken(admin.getId().toString(), admin.getUsername());
        log.info("JWT Token生成成功");

        // 6、构建返回对象
        AdminLoginVO adminLoginVO = AdminLoginVO.builder()
                .id(admin.getId())
                .username(admin.getUsername())
                .name(admin.getName())
                .phone(admin.getPhone())
                .sex(admin.getSex())
                .idNumber(admin.getIdNumber())
                .avatar(admin.getAvatar())
                .status(admin.getStatus())
                .token(token)
                .build();

        log.info("管理员登录成功，返回用户信息：{}", admin.getUsername());
        return adminLoginVO;
    }

    @Override
    public AdminLoginVO getById(Long id) {
        Admin admin = adminMapper.getById(id);
        
        if (admin == null) {
            throw new RuntimeException("管理员不存在");
        }
        
        AdminLoginVO adminLoginVO = AdminLoginVO.builder()
                .id(admin.getId())
                .username(admin.getUsername())
                .name(admin.getName())
                .phone(admin.getPhone())
                .sex(admin.getSex())
                .idNumber(admin.getIdNumber())
                .avatar(admin.getAvatar())
                .status(admin.getStatus())
                .build();
        return adminLoginVO;
    }
}
