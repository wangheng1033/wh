# RBAC管理系统

基于SpringBoot 3.2.3 + Vue3 + MySQL的前后端分离权限管理系统

## 技术栈

### 后端
- SpringBoot 3.2.3
- Spring Data JPA
- MySQL 8.0+
- Lombok

### 前端
- Vue 3.4
- Vue Router 4
- Pinia
- Element Plus
- Axios
- Vite

## 功能特性

- 用户管理：用户的增删改查、分配角色
- 角色管理：角色的增删改查、分配权限
- 权限管理：权限的增删改查、支持菜单/按钮/接口三种类型

## 项目结构

```
rbac-system/
├── backend/                    # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/rbac/
│   │   │   │   ├── controller/    # 控制器
│   │   │   │   ├── service/       # 服务层
│   │   │   │   ├── repository/    # 数据访问层
│   │   │   │   ├── entity/        # 实体类
│   │   │   │   ├── dto/           # 数据传输对象
│   │   │   │   └── exception/     # 异常处理
│   │   │   └── resources/
│   │   │       ├── application.yml    # 配置文件
│   │   │       └── db/init.sql        # 数据库初始化脚本
│   │   └── pom.xml
│   └──
├── frontend/                   # 前端项目
│   ├── src/
│   │   ├── api/                # API接口
│   │   ├── router/             # 路由配置
│   │   ├── utils/              # 工具类
│   │   └── views/              # 页面组件
│   │       └── system/
│   │           ├── User.vue        # 用户管理
│   │           ├── Role.vue        # 角色管理
│   │           └── Permission.vue  # 权限管理
│   ├── package.json
│   └── vite.config.js
```

## 快速开始

### 1. 数据库配置

```sql
# 执行数据库初始化脚本
source backend/src/main/resources/db/init.sql
```

或手动创建数据库并执行SQL脚本。

### 2. 后端启动

```bash
cd backend

# 修改数据库配置（如果需要）
# 编辑 src/main/resources/application.yml
# 修改 datasource.url、username、password

# 使用Maven启动
mvn spring-boot:run
```

后端服务将在 http://localhost:8080 启动

### 3. 前端启动

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端服务将在 http://localhost:3000 启动

## 默认账号

- 用户名：admin
- 密码：123456

## API接口

### 用户管理
- GET /api/users - 获取用户列表
- GET /api/users/{id} - 获取用户详情
- POST /api/users - 创建用户
- PUT /api/users/{id} - 更新用户
- DELETE /api/users/{id} - 删除用户
- GET /api/users/{id}/roles - 获取用户角色

### 角色管理
- GET /api/roles - 获取角色列表
- GET /api/roles/all - 获取所有角色
- GET /api/roles/{id} - 获取角色详情
- POST /api/roles - 创建角色
- PUT /api/roles/{id} - 更新角色
- DELETE /api/roles/{id} - 删除角色
- GET /api/roles/{id}/permissions - 获取角色权限

### 权限管理
- GET /api/permissions - 获取权限列表
- GET /api/permissions/all - 获取所有权限
- GET /api/permissions/tree - 获取权限树
- GET /api/permissions/{id} - 获取权限详情
- POST /api/permissions - 创建权限
- PUT /api/permissions/{id} - 更新权限
- DELETE /api/permissions/{id} - 删除权限

## 数据库表结构

- sys_user - 用户表
- sys_role - 角色表
- sys_permission - 权限表
- sys_user_role - 用户角色关联表
- sys_role_permission - 角色权限关联表
# wh
