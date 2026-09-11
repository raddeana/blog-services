## blog-user-services
provide users, roles and permissions services

### 服务概览

本服务基于 RBAC 模型提供用户、角色、权限及关联管理，模块划分如下：

| 模块 | 职责 |
|------|------|
| `users` | 用户 CRUD |
| `roles` | 角色 CRUD |
| `permissions` | 权限 CRUD |
| `userroles` | 用户 ↔ 角色关联（分配 / 查询 / 移除） |
| `rolepermissions` | 角色 ↔ 权限关联（分配 / 查询 / 移除） |

存储层使用 MyBatis Plus + MySQL，应用启动时自动执行 `classpath:schema.sql` 幂等建表。

### API 一览

#### 用户 `/api/users`
- `GET    /api/users`            查询用户列表
- `GET    /api/users/{id}`      查询单个用户
- `POST   /api/users`           创建用户
- `PUT    /api/users/{id}`      更新用户
- `DELETE /api/users/{id}`      删除用户

#### 角色 `/api/roles`
- `GET    /api/roles`                       查询角色列表
- `GET    /api/roles/{id}`                  查询单个角色
- `POST   /api/roles`                       创建角色
- `PUT    /api/roles/{id}`                  更新角色
- `DELETE /api/roles/{id}`                  删除角色

#### 权限 `/api/permissions`
- `GET    /api/permissions`          查询权限列表
- `GET    /api/permissions/{id}`     查询单个权限
- `POST   /api/permissions`          创建权限
- `PUT    /api/permissions/{id}`     更新权限
- `DELETE /api/permissions/{id}`     删除权限

#### 用户-角色关联 `/api/users/{userId}/roles`
- `GET    /api/users/{userId}/roles`              查询用户的角色列表
- `POST   /api/users/{userId}/roles`             给用户分配角色（批量），body: `{"roleIds":[1,2]}`
- `DELETE /api/users/{userId}/roles/{roleId}`    移除用户的单个角色

#### 角色-权限关联 `/api/roles/{roleId}/permissions`
- `GET    /api/roles/{roleId}/permissions`                    查询角色的权限列表
- `POST   /api/roles/{roleId}/permissions`                   给角色分配权限（批量），body: `{"permissionIds":[1,2]}`
- `DELETE /api/roles/{roleId}/permissions/{permissionId}`    移除角色的单个权限

### 数据库准备

数据源连接 `jdbc:mysql://localhost:3306/blog_db`（root 空密码，见 `application.properties`）。

首次启动前需先创建库（建表由应用启动时自动执行 `schema.sql`）：

```sql
CREATE DATABASE IF NOT EXISTS blog_db DEFAULT CHARACTER SET utf8mb4;
```

### 运行测试

执行所有单元测试（Mockito mock Mapper 层，不依赖数据库）：

```bash
mvn test
```

执行指定测试类：

```bash
mvn test -Dtest=UserServiceImplTest,RoleServiceImplTest,PermissionServiceImplTest,UserRoleServiceImplTest,RolePermissionServiceImplTest
```

测试覆盖 5 个 ServiceImpl 的全部 CRUD 与关联逻辑边界场景，共 44 个用例。

### 启动服务

编译并启动（默认端口 8080）：

```bash
mvn spring-boot:run
```

或打包后运行：

```bash
mvn clean package
java -jar target/services-0.0.1-SNAPSHOT.war
```

启动成功后访问 API 示例：

```bash
# 创建角色
curl -X POST http://localhost:8080/api/roles \
  -H "Content-Type: application/json" \
  -d '{"roleName":"管理员","roleCode":"ROLE_ADMIN","description":"系统管理员"}'

# 给角色分配权限
curl -X POST http://localhost:8080/api/roles/1/permissions \
  -H "Content-Type: application/json" \
  -d '{"permissionIds":[1,2]}'
```

Postman 测试集合位于 `postman/` 目录，可直接导入使用。

### 依赖
- spring-boot-starter-web
- fastjson
- commons-lang3
- mybatis-spring-boot-starter
- spring-boot-starter-data-redis
- mysql-connector-j
- spring-boot-starter-tomcat
- junit
- ...