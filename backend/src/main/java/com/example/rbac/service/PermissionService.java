package com.example.rbac.service;

import com.example.rbac.dto.PermissionDTO;
import com.example.rbac.entity.Permission;
import com.example.rbac.repository.PermissionRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PermissionService {
    
    @Autowired
    private PermissionRepository permissionRepository;
    
    public Page<Permission> getPermissionList(String permissionName, String resourceType, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.ASC, "sortOrder"));
        return permissionRepository.searchPermissions(permissionName, resourceType, pageable);
    }
    
    public List<Permission> getAllPermissions() {
        return permissionRepository.findByDeleted(0);
    }
    
    public List<Permission> getPermissionTree() {
        List<Permission> allPermissions = permissionRepository.findByDeleted(0);
        return buildTree(allPermissions, 0L);
    }
    
    private List<Permission> buildTree(List<Permission> permissions, Long parentId) {
        List<Permission> tree = new ArrayList<>();
        for (Permission permission : permissions) {
            if (permission.getParentId().equals(parentId)) {
                tree.add(permission);
            }
        }
        return tree;
    }
    
    public Permission getPermissionById(Long id) {
        return permissionRepository.findById(id).orElse(null);
    }
    
    @Transactional
    public Permission createPermission(PermissionDTO permissionDTO) {
        if (permissionRepository.existsByPermissionCode(permissionDTO.getPermissionCode())) {
            throw new RuntimeException("权限编码已存在");
        }
        
        Permission permission = new Permission();
        BeanUtils.copyProperties(permissionDTO, permission);
        if (permission.getParentId() == null) {
            permission.setParentId(0L);
        }
        return permissionRepository.save(permission);
    }
    
    @Transactional
    public Permission updatePermission(PermissionDTO permissionDTO) {
        Permission permission = permissionRepository.findById(permissionDTO.getId())
                .orElseThrow(() -> new RuntimeException("权限不存在"));
        
        if (!permission.getPermissionCode().equals(permissionDTO.getPermissionCode()) 
                && permissionRepository.existsByPermissionCode(permissionDTO.getPermissionCode())) {
            throw new RuntimeException("权限编码已存在");
        }
        
        permission.setPermissionName(permissionDTO.getPermissionName());
        permission.setPermissionCode(permissionDTO.getPermissionCode());
        permission.setResourceType(permissionDTO.getResourceType());
        permission.setParentId(permissionDTO.getParentId() != null ? permissionDTO.getParentId() : 0L);
        permission.setPath(permissionDTO.getPath());
        permission.setComponent(permissionDTO.getComponent());
        permission.setIcon(permissionDTO.getIcon());
        permission.setSortOrder(permissionDTO.getSortOrder());
        permission.setStatus(permissionDTO.getStatus());
        
        return permissionRepository.save(permission);
    }
    
    @Transactional
    public void deletePermission(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("权限不存在"));
        permission.setDeleted(1);
        permissionRepository.save(permission);
    }
}
