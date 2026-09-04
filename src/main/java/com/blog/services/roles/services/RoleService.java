package com.blog.services.roles.services;

import com.blog.services.roles.models.dto.RoleDTO;
import com.blog.services.roles.models.vo.CreateRoleVO;

import java.util.List;

/**
 * 角色服务接口
 */
public interface RoleService {

    /**
     * 创建角色
     */
    RoleDTO createRole(CreateRoleVO vo);

    /**
     * 根据ID查询角色
     */
    RoleDTO getRoleById(Long id);

    /**
     * 查询所有角色
     */
    List<RoleDTO> listRoles();

    /**
     * 更新角色
     */
    RoleDTO updateRole(Long id, CreateRoleVO vo);

    /**
     * 删除角色
     */
    void deleteRole(Long id);
}
