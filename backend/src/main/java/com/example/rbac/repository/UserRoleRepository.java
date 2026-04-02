package com.example.rbac.repository;

import com.example.rbac.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    
    List<UserRole> findByUserId(Long userId);
    
    List<UserRole> findByRoleId(Long roleId);
    
    void deleteByUserId(Long userId);
    
    void deleteByRoleId(Long roleId);
    
    @Modifying
    @Query("DELETE FROM UserRole ur WHERE ur.userId = :userId AND ur.roleId IN :roleIds")
    void deleteByUserIdAndRoleIds(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);
    
    boolean existsByUserIdAndRoleId(Long userId, Long roleId);
}
