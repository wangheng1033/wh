package com.example.rbac.repository;

import com.example.rbac.entity.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    
    List<Permission> findByDeleted(Integer deleted);
    
    List<Permission> findByParentIdAndDeleted(Long parentId, Integer deleted);
    
    Optional<Permission> findByPermissionCode(String permissionCode);
    
    @Query("SELECT p FROM Permission p WHERE p.deleted = 0 AND (:permissionName IS NULL OR p.permissionName LIKE %:permissionName%) AND (:resourceType IS NULL OR p.resourceType = :resourceType)")
    Page<Permission> searchPermissions(@Param("permissionName") String permissionName, @Param("resourceType") String resourceType, Pageable pageable);
    
    boolean existsByPermissionCode(String permissionCode);
}
