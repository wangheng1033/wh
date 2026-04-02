package com.example.rbac.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PermissionDTO {
    private Long id;
    
    @NotBlank(message = "权限名称不能为空")
    @Size(max = 50, message = "权限名称长度不能超过50")
    private String permissionName;
    
    @NotBlank(message = "权限编码不能为空")
    @Size(max = 100, message = "权限编码长度不能超过100")
    private String permissionCode;
    
    @NotBlank(message = "资源类型不能为空")
    private String resourceType;
    
    private Long parentId;
    
    @Size(max = 200, message = "路径长度不能超过200")
    private String path;
    
    @Size(max = 200, message = "组件路径长度不能超过200")
    private String component;
    
    @Size(max = 50, message = "图标长度不能超过50")
    private String icon;
    
    private Integer sortOrder;
    
    private Integer status;
}
