package com.blog.services.rolepermissions.services;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.services.permissions.mappers.PermissionMapper;
import com.blog.services.permissions.models.Permission;
import com.blog.services.permissions.models.dto.PermissionDTO;
import com.blog.services.rolepermissions.mappers.RolePermissionMapper;
import com.blog.services.rolepermissions.models.RolePermission;
import com.blog.services.roles.services.RoleService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 角色-权限关联服务实现类
 */
@Service
public class RolePermissionServiceImpl implements RolePermissionService {

    @Resource
    private RolePermissionMapper rolePermissionMapper;

    @Resource
    private PermissionMapper permissionMapper;

    @Resource
    private RoleService roleService;

    @Override
    public List<PermissionDTO> getRolePermissions(Long roleId) {
        List<PermissionDTO> result = new ArrayList<>();
        List<RolePermission> relations = rolePermissionMapper.selectList(
                new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getRoleId, roleId));
        for (RolePermission relation : relations) {
            Permission permission = permissionMapper.selectById(relation.getPermissionId());
            if (permission != null) {
                result.add(convertToDTO(permission));
            }
        }
        return result;
    }

    @Override
    public List<PermissionDTO> assignPermissions(Long roleId, List<Long> permissionIds) {
        // 校验角色存在
        if (roleService.getRoleById(roleId) == null) {
            throw new IllegalArgumentException("角色不存在，ID: " + roleId);
        }
        for (Long permissionId : permissionIds) {
            // 校验权限存在
            if (permissionMapper.selectById(permissionId) == null) {
                continue;
            }
            // 已存在关联则跳过（唯一约束兜底）
            Long exists = rolePermissionMapper.selectCount(
                    new LambdaQueryWrapper<RolePermission>()
                            .eq(RolePermission::getRoleId, roleId)
                            .eq(RolePermission::getPermissionId, permissionId));
            if (exists != null && exists > 0) {
                continue;
            }
            RolePermission relation = new RolePermission();
            relation.setRoleId(roleId);
            relation.setPermissionId(permissionId);
            rolePermissionMapper.insert(relation);
        }
        return getRolePermissions(roleId);
    }

    @Override
    public boolean removePermission(Long roleId, Long permissionId) {
        int rows = rolePermissionMapper.delete(
                new LambdaQueryWrapper<RolePermission>()
                        .eq(RolePermission::getRoleId, roleId)
                        .eq(RolePermission::getPermissionId, permissionId));
        return rows > 0;
    }

    private PermissionDTO convertToDTO(Permission permission) {
        PermissionDTO dto = new PermissionDTO();
        dto.setId(permission.getId());
        dto.setPermissionName(permission.getPermissionName());
        dto.setPermissionCode(permission.getPermissionCode());
        dto.setResourceType(permission.getResourceType());
        dto.setResourcePath(permission.getResourcePath());
        dto.setDescription(permission.getDescription());
        dto.setStatus(permission.getStatus());
        dto.setCreateTime(permission.getCreateTime());
        dto.setUpdateTime(permission.getUpdateTime());
        return dto;
    }
}
