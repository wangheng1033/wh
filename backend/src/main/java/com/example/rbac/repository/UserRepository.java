package com.example.rbac.repository;

import com.example.rbac.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByUsernameAndDeleted(String username, Integer deleted);
    
    Page<User> findByDeleted(Integer deleted, Pageable pageable);
    
    List<User> findByDeleted(Integer deleted);
    
    @Query("SELECT u FROM User u WHERE u.deleted = 0 AND (:username IS NULL OR u.username LIKE %:username%) AND (:status IS NULL OR u.status = :status)")
    Page<User> searchUsers(@Param("username") String username, @Param("status") Integer status, Pageable pageable);
    
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.username = :username AND u.deleted = 0")
    boolean existsByUsernameAndNotDeleted(@Param("username") String username);
}
