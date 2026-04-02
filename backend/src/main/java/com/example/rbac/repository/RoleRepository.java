package com.example.rbac.repository;

import com.example.rbac.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    Optional<Role> findByRoleCode(String roleCode);
    
    List<Role> findByDeleted(Integer deleted);
    
    Page<Role> findByDeleted(Integer deleted, Pageable pageable);
    
    @Query("SELECT r FROM Role r WHERE r.deleted = 0 AND (:roleName IS NULL OR r.roleName LIKE %:roleName%) AND (:status IS NULL OR r.status = :status)")
    Page<Role> searchRoles(@Param("roleName") String roleName, @Param("status") Integer status, Pageable pageable);
    
    boolean existsByRoleCodeAndDeleted(String roleCode, Integer deleted);
}
