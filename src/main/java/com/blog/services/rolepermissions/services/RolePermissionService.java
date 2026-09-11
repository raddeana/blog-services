package com.blog.services.rolepermissions.services;

import com.blog.services.permissions.models.dto.PermissionDTO;

import java.util.List;

/**
 * 角色-权限关联服务接口
 */
public interface RolePermissionService {

    /**
     * 查询角色的权限列表
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<PermissionDTO> getRolePermissions(Long roleId);

    /**
     * 给角色分配权限（批量）
     *
     * @param roleId        角色ID
     * @param permissionIds 权限ID列表
     * @return 分配后角色的全部权限列表
     */
    List<PermissionDTO> assignPermissions(Long roleId, List<Long> permissionIds);

    /**
     * 移除角色的单个权限
     *
     * @param roleId       角色ID
     * @param permissionId 权限ID
     * @return 是否移除成功
     */
    boolean removePermission(Long roleId, Long permissionId);
}
