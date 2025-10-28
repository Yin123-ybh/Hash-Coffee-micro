package com.coffee.userservice.controller;

import com.coffee.common.result.Result;
import com.coffee.common.utils.JwtUtils;
import com.coffee.userservice.dto.AdminLoginDTO;
import com.coffee.userservice.service.AdminService;
import com.coffee.userservice.vo.AdminLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public Result<AdminLoginVO> login(@RequestBody AdminLoginDTO adminLoginDTO) {
        log.info("管理员登录：{}", adminLoginDTO);
        try {
            AdminLoginVO adminLoginVO = adminService.login(adminLoginDTO);
            log.info("管理员登录成功");
            return Result.success(adminLoginVO);
        } catch (Exception e) {
            log.error("管理员登录异常：", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取管理员信息
     */
    @GetMapping("/info")
    public Result<AdminLoginVO> info(HttpServletRequest request) {
        try {
            // 从请求头获取token
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                return Result.error("未授权访问");
            }
            
            token = token.substring(7);
            String adminId = JwtUtils.getUserIdFromToken(token);
            if (adminId == null) {
                return Result.error("令牌无效");
            }
            
            AdminLoginVO adminInfo = adminService.getById(Long.valueOf(adminId));
            return Result.success(adminInfo);
        } catch (Exception e) {
            log.error("获取管理员信息异常：", e);
            return Result.error(e.getMessage());
        }
    }
}
