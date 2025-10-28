package com.coffee.userservice.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 管理员登录DTO
 */
@Data
public class AdminLoginDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    //用户名
    private String username;

    //密码
    private String password;
}
