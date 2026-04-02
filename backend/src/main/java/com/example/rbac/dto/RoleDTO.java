package com.example.rbac.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class RoleDTO {
    private Long id;
    
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 50, message = "角色名称长度不能超过50")
    private String roleName;
    
    @NotBlank(message = "角色编码不能为空")
    @Size(max = 50, message = "角色编码长度不能超过50")
    private String roleCode;
    
    @Size(max = 200, message = "描述长度不能超过200")
    private String description;
    
    private Integer status;
    
    private List<Long> permissionIds;
}
