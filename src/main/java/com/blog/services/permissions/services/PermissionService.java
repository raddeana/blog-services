package com.blog.services.permissions.services;

import com.blog.services.permissions.models.dto.PermissionDTO;
import com.blog.services.permissions.models.vo.PermissionVO;

import java.util.List;

/**
 * 权限服务接口
 */
public interface PermissionService {

    /**
     * 创建权限
     */
    PermissionDTO createPermission(PermissionVO vo);

    /**
     * 根据ID查询权限
     */
    PermissionDTO getPermissionById(Long id);

    /**
     * 查询所有权限
     */
    List<PermissionDTO> listPermissions();

    /**
     * 更新权限
     */
    PermissionDTO updatePermission(Long id, PermissionVO vo);

    /**
     * 删除权限
     */
    void deletePermission(Long id);
}
