package com.blog.services.roles.services;

import com.blog.services.roles.mappers.RoleMapper;
import com.blog.services.roles.models.Role;
import com.blog.services.roles.models.dto.RoleDTO;
import com.blog.services.roles.models.vo.CreateRoleVO;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 角色服务实现类
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Resource
    private RoleMapper roleMapper;

    @Override
    public RoleDTO createRole(CreateRoleVO vo) {
        Role role = new Role();
        role.setRoleName(vo.getRoleName());
        role.setRoleCode(vo.getRoleCode());
        role.setDescription(vo.getDescription());
        role.setStatus(1);
        role.setCreateTime(System.currentTimeMillis());
        role.setUpdateTime(System.currentTimeMillis());
        roleMapper.insert(role);
        return convertToDTO(role);
    }

    @Override
    public RoleDTO getRoleById(Long id) {
        Role role = roleMapper.selectById(id);
        return role != null ? convertToDTO(role) : null;
    }

    @Override
    public List<RoleDTO> listRoles() {
        List<RoleDTO> result = new ArrayList<>();
        for (Role role : roleMapper.selectList(null)) {
            result.add(convertToDTO(role));
        }
        return result;
    }

    @Override
    public RoleDTO updateRole(Long id, CreateRoleVO vo) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            return null;
        }
        if (vo.getRoleName() != null) {
            role.setRoleName(vo.getRoleName());
        }
        if (vo.getRoleCode() != null) {
            role.setRoleCode(vo.getRoleCode());
        }
        if (vo.getDescription() != null) {
            role.setDescription(vo.getDescription());
        }
        role.setUpdateTime(System.currentTimeMillis());
        roleMapper.updateById(role);
        return convertToDTO(role);
    }

    @Override
    public void deleteRole(Long id) {
        roleMapper.deleteById(id);
    }

    private RoleDTO convertToDTO(Role role) {
        RoleDTO dto = new RoleDTO();
        dto.setId(role.getId());
        dto.setRoleName(role.getRoleName());
        dto.setRoleCode(role.getRoleCode());
        dto.setDescription(role.getDescription());
        dto.setStatus(role.getStatus());
        dto.setCreateTime(role.getCreateTime());
        dto.setUpdateTime(role.getUpdateTime());
        return dto;
    }
}
