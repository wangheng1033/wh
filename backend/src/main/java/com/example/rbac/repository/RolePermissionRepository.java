package com.example.rbac.repository;

import com.example.rbac.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    
    List<RolePermission> findByRoleId(Long roleId);
    
    List<RolePermission> findByPermissionId(Long permissionId);
    
    void deleteByRoleId(Long roleId);
    
    void deleteByPermissionId(Long permissionId);
    
    @Modifying
    @Query("DELETE FROM RolePermission rp WHERE rp.roleId = :roleId AND rp.permissionId IN :permissionIds")
    void deleteByRoleIdAndPermissionIds(@Param("roleId") Long roleId, @Param("permissionIds") List<Long> permissionIds);
    
    boolean existsByRoleIdAndPermissionId(Long roleId, Long permissionId);
}
