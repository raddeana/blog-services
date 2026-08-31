package com.blog.services.roles.services;

import com.blog.services.roles.models.Role;
import com.blog.services.roles.models.dto.RoleDTO;
import com.blog.services.roles.models.vo.CreateRoleVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 角色服务实现类
 */
@Service
public class RoleServiceImpl implements RoleService {

    private final Map<Long, Role> roleRepository = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public RoleDTO createRole(CreateRoleVO vo) {
        Role role = new Role();
        role.setId(idGenerator.getAndIncrement());
        role.setRoleName(vo.getRoleName());
        role.setRoleCode(vo.getRoleCode());
        role.setDescription(vo.getDescription());
        role.setStatus(1);
        role.setCreateTime(System.currentTimeMillis());
        role.setUpdateTime(System.currentTimeMillis());
        roleRepository.put(role.getId(), role);
        return convertToDTO(role);
    }

    @Override
    public RoleDTO getRoleById(Long id) {
        Role role = roleRepository.get(id);
        return role != null ? convertToDTO(role) : null;
    }

    @Override
    public List<RoleDTO> listRoles() {
        List<RoleDTO> result = new ArrayList<>();
        for (Role role : roleRepository.values()) {
            result.add(convertToDTO(role));
        }
        return result;
    }

    @Override
    public RoleDTO updateRole(Long id, CreateRoleVO vo) {
        Role role = roleRepository.get(id);
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
        return convertToDTO(role);
    }

    @Override
    public void deleteRole(Long id) {
        roleRepository.remove(id);
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
