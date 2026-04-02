package com.example.rbac.controller;

import com.example.rbac.dto.ApiResponse;
import com.example.rbac.dto.AssignRoleDTO;
import com.example.rbac.dto.UserDTO;
import com.example.rbac.entity.User;
import com.example.rbac.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    public ApiResponse<Map<String, Object>> getUserList(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<User> userPage = userService.getUserList(username, status, page, size);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", userPage.getContent());
        result.put("total", userPage.getTotalElements());
        result.put("page", page);
        result.put("size", size);
        
        return ApiResponse.success(result);
    }
    
    @GetMapping("/{id}")
    public ApiResponse<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ApiResponse.success(user);
    }
    
    @GetMapping("/{id}/roles")
    public ApiResponse<List<Long>> getUserRoleIds(@PathVariable Long id) {
        List<Long> roleIds = userService.getUserRoleIds(id);
        return ApiResponse.success(roleIds);
    }
    
    @PostMapping
    public ApiResponse<User> createUser(@RequestBody UserDTO userDTO) {
        if (userDTO.getUsername() == null || userDTO.getUsername().trim().isEmpty()) {
            return ApiResponse.error(400, "用户名不能为空");
        }
        if (userDTO.getUsername().length() < 3 || userDTO.getUsername().length() > 50) {
            return ApiResponse.error(400, "用户名长度必须在3-50之间");
        }
        User user = userService.createUser(userDTO);
        return ApiResponse.success("创建成功", user);
    }
    
    @PutMapping("/{id}")
    public ApiResponse<User> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        userDTO.setId(id);
        User user = userService.updateUser(userDTO);
        return ApiResponse.success("更新成功", user);
    }
    
    @PutMapping("/{id}/roles")
    public ApiResponse<Void> assignRoles(@PathVariable Long id, @RequestBody AssignRoleDTO assignRoleDTO) {
        userService.assignRoles(id, assignRoleDTO.getRoleIds());
        return ApiResponse.success("分配成功", null);
    }
    
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.success("删除成功", null);
    }
}
