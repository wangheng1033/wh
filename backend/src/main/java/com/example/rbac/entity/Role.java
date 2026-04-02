package com.example.rbac.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "sys_role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "role_name", nullable = false, unique = true, length = 50)
    private String roleName;
    
    @Column(name = "role_code", nullable = false, unique = true, length = 50)
    private String roleCode;
    
    @Column(length = 200)
    private String description;
    
    @Column(nullable = false)
    private Integer status = 1;
    
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    
    @Column(nullable = false)
    private Integer deleted = 0;
    
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}
