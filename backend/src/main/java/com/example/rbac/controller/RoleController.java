package com.example.rbac.controller;

import com.example.rbac.dto.ApiResponse;
import com.example.rbac.dto.AssignPermissionDTO;
import com.example.rbac.dto.RoleDTO;
import com.example.rbac.entity.Role;
import com.example.rbac.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "*")
public class RoleController {
    
    @Autowired
    private RoleService roleService;
    
    @GetMapping
    public ApiResponse<Map<String, Object>> getRoleList(
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Role> rolePage = roleService.getRoleList(roleName, status, page, size);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", rolePage.getContent());
        result.put("total", rolePage.getTotalElements());
        result.put("page", page);
        result.put("size", size);
        
        return ApiResponse.success(result);
    }
    
    @GetMapping("/all")
    public ApiResponse<List<Role>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return ApiResponse.success(roles);
    }
    
    @GetMapping("/{id}")
    public ApiResponse<Role> getRoleById(@PathVariable Long id) {
        Role role = roleService.getRoleById(id);
        return ApiResponse.success(role);
    }
    
    @GetMapping("/{id}/permissions")
    public ApiResponse<List<Long>> getRolePermissionIds(@PathVariable Long id) {
        List<Long> permissionIds = roleService.getRolePermissionIds(id);
        return ApiResponse.success(permissionIds);
    }
    
    @PostMapping
    public ApiResponse<Role> createRole(@RequestBody RoleDTO roleDTO) {
        if (roleDTO.getRoleName() == null || roleDTO.getRoleName().trim().isEmpty()) {
            return ApiResponse.error(400, "角色名称不能为空");
        }
        if (roleDTO.getRoleCode() == null || roleDTO.getRoleCode().trim().isEmpty()) {
            return ApiResponse.error(400, "角色编码不能为空");
        }
        Role role = roleService.createRole(roleDTO);
        return ApiResponse.success("创建成功", role);
    }
    
    @PutMapping("/{id}")
    public ApiResponse<Role> updateRole(@PathVariable Long id, @RequestBody RoleDTO roleDTO) {
        roleDTO.setId(id);
        Role role = roleService.updateRole(roleDTO);
        return ApiResponse.success("更新成功", role);
    }
    
    @PutMapping("/{id}/permissions")
    public ApiResponse<Void> assignPermissions(@PathVariable Long id, @RequestBody AssignPermissionDTO assignPermissionDTO) {
        roleService.assignPermissions(id, assignPermissionDTO.getPermissionIds());
        return ApiResponse.success("分配成功", null);
    }
    
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ApiResponse.success("删除成功", null);
    }
}
