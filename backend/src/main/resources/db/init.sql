-- 创建数据库
CREATE DATABASE IF NOT EXISTS rbac_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE rbac_db;

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    status TINYINT DEFAULT 1 COMMENT '状态 0:禁用 1:启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除 0:否 1:是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '角色ID',
    role_name VARCHAR(50) NOT NULL UNIQUE COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    description VARCHAR(200) COMMENT '角色描述',
    status TINYINT DEFAULT 1 COMMENT '状态 0:禁用 1:启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除 0:否 1:是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 权限表
CREATE TABLE IF NOT EXISTS sys_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '权限ID',
    permission_name VARCHAR(50) NOT NULL COMMENT '权限名称',
    permission_code VARCHAR(100) NOT NULL UNIQUE COMMENT '权限编码',
    resource_type VARCHAR(20) NOT NULL COMMENT '资源类型 menu:菜单 button:按钮 api:接口',
    parent_id BIGINT DEFAULT 0 COMMENT '父级ID',
    path VARCHAR(200) COMMENT '路径',
    component VARCHAR(200) COMMENT '组件路径',
    icon VARCHAR(50) COMMENT '图标',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态 0:禁用 1:启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除 0:否 1:是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS sys_role_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_role_permission (role_id, permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 初始化数据
-- 插入默认管理员
INSERT INTO sys_user (username, password, email, phone) VALUES 
('admin', 'e10adc3949ba59abbe56e057f20f883e', 'admin@example.com', '13800138000');

-- 插入默认角色
INSERT INTO sys_role (role_name, role_code, description) VALUES 
('超级管理员', 'SUPER_ADMIN', '拥有所有权限'),
('普通用户', 'USER', '普通用户角色');

-- 插入默认权限
INSERT INTO sys_permission (permission_name, permission_code, resource_type, parent_id, path, component, icon, sort_order) VALUES 
('系统管理', 'system', 'menu', 0, '/system', 'Layout', 'Setting', 1),
('用户管理', 'system:user', 'menu', 1, '/system/user', 'system/user/index', 'User', 1),
('角色管理', 'system:role', 'menu', 1, '/system/role', 'system/role/index', 'UserFilled', 2),
('权限管理', 'system:permission', 'menu', 1, '/system/permission', 'system/permission/index', 'Lock', 3),
('新增用户', 'system:user:add', 'button', 2, NULL, NULL, NULL, 1),
('编辑用户', 'system:user:edit', 'button', 2, NULL, NULL, NULL, 2),
('删除用户', 'system:user:delete', 'button', 2, NULL, NULL, NULL, 3),
('新增角色', 'system:role:add', 'button', 3, NULL, NULL, NULL, 1),
('编辑角色', 'system:role:edit', 'button', 3, NULL, NULL, NULL, 2),
('删除角色', 'system:role:delete', 'button', 3, NULL, NULL, NULL, 3);

-- 给管理员分配超级管理员角色
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);

-- 给超级管理员角色分配所有权限
INSERT INTO sys_role_permission (role_id, permission_id) 
SELECT 1, id FROM sys_permission;

-- 给普通用户角色分配部分权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES 
(2, 1), (2, 2);
