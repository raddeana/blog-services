package com.blog.services.permissions.services;

import com.blog.services.permissions.mappers.PermissionMapper;
import com.blog.services.permissions.models.Permission;
import com.blog.services.permissions.models.dto.PermissionDTO;
import com.blog.services.permissions.models.vo.PermissionVO;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 权限服务实现类
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    @Resource
    private PermissionMapper permissionMapper;

    @Override
    public PermissionDTO createPermission(PermissionVO vo) {
        Permission permission = new Permission();
        permission.setPermissionName(vo.getPermissionName());
        permission.setPermissionCode(vo.getPermissionCode());
        permission.setResourceType(vo.getResourceType());
        permission.setResourcePath(vo.getResourcePath());
        permission.setDescription(vo.getDescription());
        permission.setStatus(1);
        permission.setCreateTime(System.currentTimeMillis());
        permission.setUpdateTime(System.currentTimeMillis());
        permissionMapper.insert(permission);
        return convertToDTO(permission);
    }

    @Override
    public PermissionDTO getPermissionById(Long id) {
        Permission permission = permissionMapper.selectById(id);
        return permission != null ? convertToDTO(permission) : null;
    }

    @Override
    public List<PermissionDTO> listPermissions() {
        List<PermissionDTO> result = new ArrayList<>();
        for (Permission permission : permissionMapper.selectList(null)) {
            result.add(convertToDTO(permission));
        }
        return result;
    }

    @Override
    public PermissionDTO updatePermission(Long id, PermissionVO vo) {
        Permission permission = permissionMapper.selectById(id);
        if (permission == null) {
            return null;
        }
        if (vo.getPermissionName() != null) {
            permission.setPermissionName(vo.getPermissionName());
        }
        if (vo.getPermissionCode() != null) {
            permission.setPermissionCode(vo.getPermissionCode());
        }
        if (vo.getResourceType() != null) {
            permission.setResourceType(vo.getResourceType());
        }
        if (vo.getResourcePath() != null) {
            permission.setResourcePath(vo.getResourcePath());
        }
        if (vo.getDescription() != null) {
            permission.setDescription(vo.getDescription());
        }
        permission.setUpdateTime(System.currentTimeMillis());
        permissionMapper.updateById(permission);
        return convertToDTO(permission);
    }

    @Override
    public void deletePermission(Long id) {
        permissionMapper.deleteById(id);
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
