package com.example.rbac.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "sys_permission")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "permission_name", nullable = false, length = 50)
    private String permissionName;
    
    @Column(name = "permission_code", nullable = false, unique = true, length = 100)
    private String permissionCode;
    
    @Column(name = "resource_type", nullable = false, length = 20)
    private String resourceType;
    
    @Column(name = "parent_id", nullable = false)
    private Long parentId = 0L;
    
    @Column(length = 200)
    private String path;
    
    @Column(length = 200)
    private String component;
    
    @Column(length = 50)
    private String icon;
    
    @Column(name = "sort_order")
    private Integer sortOrder = 0;
    
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
