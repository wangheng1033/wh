package com.example.rbac.service;

import com.example.rbac.dto.RoleDTO;
import com.example.rbac.entity.Role;
import com.example.rbac.entity.RolePermission;
import com.example.rbac.repository.RolePermissionRepository;
import com.example.rbac.repository.RoleRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private RolePermissionRepository rolePermissionRepository;
    
    public Page<Role> getRoleList(String roleName, Integer status, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        return roleRepository.searchRoles(roleName, status, pageable);
    }
    
    public List<Role> getAllRoles() {
        return roleRepository.findByDeleted(0);
    }
    
    public Role getRoleById(Long id) {
        return roleRepository.findById(id).orElse(null);
    }
    
    public List<Long> getRolePermissionIds(Long roleId) {
        return rolePermissionRepository.findByRoleId(roleId).stream()
                .map(RolePermission::getPermissionId)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public Role createRole(RoleDTO roleDTO) {
        if (roleRepository.existsByRoleCodeAndDeleted(roleDTO.getRoleCode(), 0)) {
            throw new RuntimeException("角色编码已存在");
        }
        
        Role role = new Role();
        BeanUtils.copyProperties(roleDTO, role);
        role = roleRepository.save(role);
        
        if (roleDTO.getPermissionIds() != null && !roleDTO.getPermissionIds().isEmpty()) {
            assignPermissions(role.getId(), roleDTO.getPermissionIds());
        }
        
        return role;
    }
    
    @Transactional
    public Role updateRole(RoleDTO roleDTO) {
        Role role = roleRepository.findById(roleDTO.getId())
                .orElseThrow(() -> new RuntimeException("角色不存在"));
        
        if (roleDTO.getRoleCode() != null && !role.getRoleCode().equals(roleDTO.getRoleCode()) 
                && roleRepository.existsByRoleCodeAndDeleted(roleDTO.getRoleCode(), 0)) {
            throw new RuntimeException("角色编码已存在");
        }
        
        if (roleDTO.getRoleName() != null) {
            role.setRoleName(roleDTO.getRoleName());
        }
        if (roleDTO.getRoleCode() != null) {
            role.setRoleCode(roleDTO.getRoleCode());
        }
        if (roleDTO.getDescription() != null) {
            role.setDescription(roleDTO.getDescription());
        }
        if (roleDTO.getStatus() != null) {
            role.setStatus(roleDTO.getStatus());
        }
        role = roleRepository.save(role);
        
        if (roleDTO.getPermissionIds() != null) {
            List<RolePermission> existing = rolePermissionRepository.findByRoleId(role.getId());
            rolePermissionRepository.deleteAll(existing);
            rolePermissionRepository.flush();
            assignPermissions(role.getId(), roleDTO.getPermissionIds());
        }
        
        return role;
    }
    
    @Transactional
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("角色不存在"));
        role.setDeleted(1);
        roleRepository.save(role);
        List<RolePermission> existing = rolePermissionRepository.findByRoleId(id);
        rolePermissionRepository.deleteAll(existing);
    }
    
    @Transactional
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        List<RolePermission> existing = rolePermissionRepository.findByRoleId(roleId);
        rolePermissionRepository.deleteAll(existing);
        rolePermissionRepository.flush();
        
        if (permissionIds != null && !permissionIds.isEmpty()) {
            for (Long permissionId : permissionIds) {
                RolePermission rolePermission = new RolePermission();
                rolePermission.setRoleId(roleId);
                rolePermission.setPermissionId(permissionId);
                rolePermissionRepository.save(rolePermission);
            }
        }
    }
}
