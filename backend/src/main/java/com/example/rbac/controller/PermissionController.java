package com.example.rbac.controller;

import com.example.rbac.dto.ApiResponse;
import com.example.rbac.dto.PermissionDTO;
import com.example.rbac.entity.Permission;
import com.example.rbac.service.PermissionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/permissions")
@CrossOrigin(origins = "*")
public class PermissionController {
    
    @Autowired
    private PermissionService permissionService;
    
    @GetMapping
    public ApiResponse<Map<String, Object>> getPermissionList(
            @RequestParam(required = false) String permissionName,
            @RequestParam(required = false) String resourceType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Permission> permissionPage = permissionService.getPermissionList(permissionName, resourceType, page, size);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", permissionPage.getContent());
        result.put("total", permissionPage.getTotalElements());
        result.put("page", page);
        result.put("size", size);
        
        return ApiResponse.success(result);
    }
    
    @GetMapping("/all")
    public ApiResponse<List<Permission>> getAllPermissions() {
        List<Permission> permissions = permissionService.getAllPermissions();
        return ApiResponse.success(permissions);
    }
    
    @GetMapping("/tree")
    public ApiResponse<List<Permission>> getPermissionTree() {
        List<Permission> permissions = permissionService.getPermissionTree();
        return ApiResponse.success(permissions);
    }
    
    @GetMapping("/{id}")
    public ApiResponse<Permission> getPermissionById(@PathVariable Long id) {
        Permission permission = permissionService.getPermissionById(id);
        return ApiResponse.success(permission);
    }
    
    @PostMapping
    public ApiResponse<Permission> createPermission(@Valid @RequestBody PermissionDTO permissionDTO) {
        Permission permission = permissionService.createPermission(permissionDTO);
        return ApiResponse.success("创建成功", permission);
    }
    
    @PutMapping("/{id}")
    public ApiResponse<Permission> updatePermission(@PathVariable Long id, @Valid @RequestBody PermissionDTO permissionDTO) {
        permissionDTO.setId(id);
        Permission permission = permissionService.updatePermission(permissionDTO);
        return ApiResponse.success("更新成功", permission);
    }
    
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return ApiResponse.success("删除成功", null);
    }
}
