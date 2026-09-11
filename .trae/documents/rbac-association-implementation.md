# RBAC 关联关系实现计划

## Context（背景）

当前项目已完成 用户(users)、角色(roles)、权限(permissions) 三个模块的独立 CRUD，但三者之间缺乏关联关系，无法构成完整的 RBAC 体系。本次需要补齐：
- **用户 ↔ 角色**：给用户分配/移除角色，查询用户的角色列表
- **角色 ↔ 权限**：给角色分配/移除权限，查询角色的权限列表

实现完整关联生命周期（分配 + 查询 + 移除），为后续接口权限校验打基础。

## 设计原则

- 遵循现有模式：内存存储（`ConcurrentHashMap`）、`@Resource` 注入、`Result<T>` 统一响应、Controller 层参数校验、RESTful 路由
- 新建独立模块承载关联逻辑，职责清晰，不污染现有 users/roles/permissions 模块
- 关联 Service 注入对应基础 Service，复用其校验与查询能力（如 [RoleService.getRoleById](file:///d:/github/blog-user-services/src/main/java/com/blog/services/roles/services/RoleService.java)、[PermissionService.getPermissionById](file:///d:/github/blog-user-services/src/main/java/com/blog/services/permissions/services/PermissionService.java)）

## API 设计

### 用户-角色（嵌套在 /api/users 下）
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/users/{userId}/roles` | 查询用户的角色列表，返回 `List<RoleDTO>` |
| POST | `/api/users/{userId}/roles` | 给用户分配角色（批量），body: `{roleIds:[1,2]}` |
| DELETE | `/api/users/{userId}/roles/{roleId}` | 移除用户的单个角色 |

### 角色-权限（嵌套在 /api/roles 下）
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/roles/{roleId}/permissions` | 查询角色的权限列表，返回 `List<PermissionDTO>` |
| POST | `/api/roles/{roleId}/permissions` | 给角色分配权限（批量），body: `{permissionIds:[1,2]}` |
| DELETE | `/api/roles/{roleId}/permissions/{permissionId}` | 移除角色的单个权限 |

## 存储结构

关联关系用 `Map<Long, Set<Long>>` 存储，线程安全 Set 用 `ConcurrentHashMap.newKeySet()`：
- `UserRoleServiceImpl`：`Map<Long, Set<Long>> userRoles`（userId → roleIds）
- `RolePermissionServiceImpl`：`Map<Long, Set<Long>> rolePermissions`（roleId → permissionIds）

## 实现步骤

### 步骤 1：用户-角色模块

新建包 `com.blog.services.userroles`：

1. **VO** `userroles/models/vo/AssignRolesVO.java`
   - 字段：`List<Long> roleIds` + getter/setter，参考 [AssignPermissions 模式/CreateRoleVO](file:///d:/github/blog-user-services/src/main/java/com/blog/services/roles/models/vo/CreateRoleVO.java)

2. **Service 接口** `userroles/services/UserRoleService.java`
   - `List<RoleDTO> getUserRoles(Long userId)`
   - `List<RoleDTO> assignRoles(Long userId, List<Long> roleIds)`
   - `boolean removeRole(Long userId, Long roleId)`

3. **Service 实现** `userroles/services/UserRoleServiceImpl.java`
   - 注入 `UserService`（校验用户存在）、`RoleService`（校验角色存在 + 取 RoleDTO）
   - `assignRoles`：校验用户存在 → 过滤出存在的角色 → 加入 Set → 返回当前全部角色 DTO
   - `removeRole`：从 Set 移除，返回是否成功
   - 参考 [RoleServiceImpl](file:///d:/github/blog-user-services/src/main/java/com/blog/services/roles/services/RoleServiceImpl.java) 的 convertToDTO 复用 RoleService.getRoleById

4. **Controller** `userroles/controllers/UserRoleController.java`
   - `@RequestMapping("/api/users")`，路由 `/{userId}/roles`、`/{userId}/roles/{roleId}`
   - 参数校验：userId>0、roleIds 非空，参考 [UsersController](file:///d:/github/blog-user-services/src/main/java/com/blog/services/users/controllers/UsersController.java)
   - try-catch + Result.success/error，与现有控制器一致

### 步骤 2：角色-权限模块

新建包 `com.blog.services.rolepermissions`：

1. **VO** `rolepermissions/models/vo/AssignPermissionsVO.java`
   - 字段：`List<Long> permissionIds` + getter/setter

2. **Service 接口** `rolepermissions/services/RolePermissionService.java`
   - `List<PermissionDTO> getRolePermissions(Long roleId)`
   - `List<PermissionDTO> assignPermissions(Long roleId, List<Long> permissionIds)`
   - `boolean removePermission(Long roleId, Long permissionId)`

3. **Service 实现** `rolepermissions/services/RolePermissionServiceImpl.java`
   - 注入 `RoleService`（校验角色存在）、`PermissionService`（校验权限存在 + 取 PermissionDTO）
   - 逻辑同上，对偶设计

4. **Controller** `rolepermissions/controllers/RolePermissionController.java`
   - `@RequestMapping("/api/roles")`，路由 `/{roleId}/permissions`、`/{roleId}/permissions/{permissionId}`

### 步骤 3：编译验证

执行 `mvn compile` 确认无编译错误。

## 验证方式

1. `mvn compile` 通过编译
2. 启动应用后用 curl/Postman 端到端验证：
   - 先创建用户、角色、权限（现有接口）
   - `POST /api/users/1/roles` body `{"roleIds":[1,2]}` → 返回角色列表
   - `GET /api/users/1/roles` → 返回已分配角色
   - `POST /api/roles/1/permissions` body `{"permissionIds":[1,2]}`
   - `GET /api/roles/1/permissions` → 返回已分配权限
   - `DELETE /api/users/1/roles/2` → 移除后再 GET 确认减少
   - `DELETE /api/roles/1/permissions/2` → 移除后再 GET 确认减少
3. 异常场景：分配不存在的角色/权限 → 返回 404 或过滤提示；操作不存在的用户/角色 → 404

## 不做的事

- 不引入数据库表与 MyBatis Mapper（保持与现有内存存储一致，后续统一替换）
- 不改动现有 users/roles/permissions 模块的实体与 CRUD 代码
- 不做接口鉴权（Spring Security 配置不在本次范围）
