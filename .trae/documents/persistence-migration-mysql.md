# 持久化改造计划（内存存储 → MySQL + MyBatis Plus）

## Context（背景）

当前项目用户、角色、权限及两者的关联关系全部使用 `ConcurrentHashMap`/`Map<Long,Set<Long>>` 内存存储，应用重启即丢失。pom.xml 已引入 mybatis-plus 3.5.14、mysql-connector-j、druid，但 `application.properties` 主动排除了数据源自动装配。本次将全部 5 张表持久化到 MySQL，复用 MyBatis Plus 的 BaseMapper 能力，最小改动改造现有 ServiceImpl。

## 确认信息

- 数据库：`jdbc:mysql://localhost:3306/blog_db`，用户 `root`，密码空
- ORM：MyBatis Plus `BaseMapper`，不写 XML
- 时间字段：继续用应用层 `System.currentTimeMillis()` 填充（bigint 存毫秒），保持现有逻辑不变

## 数据库表设计（5 张表）

DDL 脚本：`src/main/resources/schema.sql`（建库 + 建表，仅作参考，手动执行）

1. `user`（id BIGINT PK AUTO_INC, username, password, email, nickname, avatar, status, create_time BIGINT, update_time BIGINT）
2. `role`（id, role_name, role_code, description, status, create_time, update_time）
3. `permission`（id, permission_name, permission_code, resource_type, resource_path, description, status, create_time, update_time）
4. `user_role`（id, user_id, role_id, UNIQUE(user_id, role_id)）
5. `role_permission`（id, role_id, permission_id, UNIQUE(role_id, permission_id)）

关联表加唯一约束，防止重复分配。

## 实现步骤

### 步骤 1：配置数据源

修改 [application.properties](file:///d:/github/blog-user-services/src/main/resources/application.properties)：
- 删除 `spring.autoconfigure.exclude` 三行
- 添加 Druid 数据源配置（url/username/password/driver）
- 添加 `mybatis-plus.configuration.map-underscore-to-camel-case=true`（表下划线 ↔ 驼峰字段自动映射）
- 添加 `mybatis-plus.global-config.db-config.id-type=auto`（主键自增）

### 步骤 2：启动类加 @MapperScan

修改 [BlogServicesApplication.java](file:///d:/github/blog-user-services/src/main/java/com/blog/services/BlogServicesApplication.java)，加 `@MapperScan("com.blog.services.**.mappers")`。

### 步骤 3：实体加 MyBatis Plus 注解

给现有 3 个实体加 `@TableName`/`@TableId`，字段无需 `@TableField`（靠下划线-驼峰自动映射）：
- [User.java](file:///d:/github/blog-user-services/src/main/java/com/blog/services/users/models/User.java) → `@TableName("user")`，id 字段加 `@TableId(type = IdType.AUTO)`
- [Role.java](file:///d:/github/blog-user-services/src/main/java/com/blog/services/roles/models/Role.java) → `@TableName("role")`
- [Permission.java](file:///d:/github/blog-user-services/src/main/java/com/blog/services/permissions/models/Permission.java) → `@TableName("permission")`

新建 2 个关联实体：
- `userroles/models/UserRole.java`：`@TableName("user_role")`，字段 id/userId/roleId
- `rolepermissions/models/RolePermission.java`：`@TableName("role_permission")`，字段 id/roleId/permissionId

### 步骤 4：创建 5 个 Mapper

每个模块的 `mappers` 包下建接口，继承 `BaseMapper<XxxEntity>`：
- `users/mappers/UserMapper.java` extends BaseMapper<User>
- `roles/mappers/RoleMapper.java` extends BaseMapper<Role>
- `permissions/mappers/PermissionMapper.java` extends BaseMapper<Permission>
- `userroles/mappers/UserRoleMapper.java` extends BaseMapper<UserRole>
- `rolepermissions/mappers/RolePermissionMapper.java` extends BaseMapper<RolePermission>

无需写 XML，BaseMapper 已提供 insert/deleteById/selectById/selectList/selectBatchIds/updateById 等。

### 步骤 5：改造 5 个 ServiceImpl

#### 基础模块（3 个）—— 单表 CRUD 直接替换
- [UserServiceImpl](file:///d:/github/blog-user-services/src/main/java/com/blog/services/users/services/UserServiceImpl.java)：注入 `UserMapper`，用 `baseMapper.insert/selectById/selectList/updateById/deleteById` 替换 `userRepository` 操作，移除 `idGenerator`（自增主键）。`convertToDTO` 保留。
- [RoleServiceImpl](file:///d:/github/blog-user-services/src/main/java/com/blog/services/roles/services/RoleServiceImpl.java)：同上模式注入 `RoleMapper`。
- [PermissionServiceImpl](file:///d:/github/blog-user-services/src/main/java/com/blog/services/permissions/services/PermissionServiceImpl.java)：注入 `PermissionMapper`。

#### 关联模块（2 个）—— 用 LambdaQueryWrapper 查询关联
- [UserRoleServiceImpl](file:///d:/github/blog-user-services/src/main/java/com/blog/services/userroles/services/UserRoleServiceImpl.java)：注入 `UserRoleMapper` + `RoleMapper`（替代原来的 RoleService 调用，直接查 Role 表）
  - `getUserRoles(userId)`：`roleMapper.selectBatchIds(roleIds)`，roleIds 由 `userRoleMapper.selectList(LambdaQueryWrapper 按 userId 查)` 转换得到
  - `assignRoles(userId, roleIds)`：遍历 roleIds，对每个用 `userRoleMapper.selectCount` 判断是否存在，不存在才 insert（配合唯一约束兜底）
  - `removeRole(userId, roleId)`：`userRoleMapper.delete(new LambdaQueryWrapper().eq(userId).eq(roleId))`，返回影响行数 >0
- [RolePermissionServiceImpl](file:///d:/github/blog-user-services/src/main/java/com/blog/services/rolepermissions/services/RolePermissionServiceImpl.java)：对偶设计，注入 `RolePermissionMapper` + `PermissionMapper`

> 校验用户/角色存在性：基础模块改造后，关联 Service 仍注入 `UserService`/`RoleService`（用其 `getUserById`/`getRoleById`），保持调用链不变，避免重复校验逻辑。

### 步骤 6：建库脚本

`src/main/resources/schema.sql` 提供 `CREATE DATABASE IF NOT EXISTS blog_db` + 5 张表 DDL，供手动执行或后续自动化。`create_time`/`update_time` 用 `BIGINT`（存毫秒，与应用层一致）。

## 验证方式

1. 本机 MySQL 执行 `schema.sql` 建库建表
2. `mvn compile` 编译通过
3. 启动应用，确认 Druid 数据源初始化成功（日志无异常）
4. 用已有 Postman 集合端到端验证：
   - 创建用户/角色/权限 → 数据库可查到记录，ID 自增
   - 分配角色/权限 → `user_role`/`role_permission` 表有记录，重复分配不报错（唯一约束兜底）
   - 查询关联列表 → 返回正确 DTO
   - 移除关联 → 关联表记录删除
   - 重启应用后再次查询，数据仍在（验证持久化成功）

## 不做的事

- 不改 Controller、VO、DTO（接口契约不变）
- 不引入事务注解（单表操作无需，关联操作的插入/删除已用唯一约束兜底）
- 不做分页、软删除等增强（保持最小改动）
- 不写 mapper XML（BaseMapper 足够）
