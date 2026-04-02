package com.example.rbac.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class UserDTO {
    private Long id;
    
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50之间")
    private String username;
    
    @Size(min = 6, max = 100, message = "密码长度必须在6-100之间")
    private String password;
    
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @Size(max = 20, message = "手机号长度不能超过20")
    private String phone;
    
    private Integer status;
    
    private List<Long> roleIds;
}
