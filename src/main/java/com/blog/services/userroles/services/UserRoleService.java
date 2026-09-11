package com.blog.services.userroles.services;

import com.blog.services.roles.models.dto.RoleDTO;

import java.util.List;

/**
 * 用户-角色关联服务接口
 */
public interface UserRoleService {

    /**
     * 查询用户的角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<RoleDTO> getUserRoles(Long userId);

    /**
     * 给用户分配角色（批量）
     *
     * @param userId   用户ID
     * @param roleIds  角色ID列表
     * @return 分配后用户的全部角色列表
     */
    List<RoleDTO> assignRoles(Long userId, List<Long> roleIds);

    /**
     * 移除用户的单个角色
     *
     * @param userId  用户ID
     * @param roleId  角色ID
     * @return 是否移除成功
     */
    boolean removeRole(Long userId, Long roleId);
}
