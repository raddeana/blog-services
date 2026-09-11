package com.blog.services.userroles.services;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.services.roles.mappers.RoleMapper;
import com.blog.services.roles.models.Role;
import com.blog.services.roles.models.dto.RoleDTO;
import com.blog.services.userroles.mappers.UserRoleMapper;
import com.blog.services.userroles.models.UserRole;
import com.blog.services.users.services.UserService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户-角色关联服务实现类
 */
@Service
public class UserRoleServiceImpl implements UserRoleService {

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private UserService userService;

    @Override
    public List<RoleDTO> getUserRoles(Long userId) {
        List<RoleDTO> result = new ArrayList<>();
        List<UserRole> relations = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId));
        for (UserRole relation : relations) {
            Role role = roleMapper.selectById(relation.getRoleId());
            if (role != null) {
                result.add(convertToDTO(role));
            }
        }
        return result;
    }

    @Override
    public List<RoleDTO> assignRoles(Long userId, List<Long> roleIds) {
        // 校验用户存在
        if (userService.getUserById(userId) == null) {
            throw new IllegalArgumentException("用户不存在，ID: " + userId);
        }
        for (Long roleId : roleIds) {
            // 校验角色存在
            if (roleMapper.selectById(roleId) == null) {
                continue;
            }
            // 已存在关联则跳过（唯一约束兜底）
            Long exists = userRoleMapper.selectCount(
                    new LambdaQueryWrapper<UserRole>()
                            .eq(UserRole::getUserId, userId)
                            .eq(UserRole::getRoleId, roleId));
            if (exists != null && exists > 0) {
                continue;
            }
            UserRole relation = new UserRole();
            relation.setUserId(userId);
            relation.setRoleId(roleId);
            userRoleMapper.insert(relation);
        }
        return getUserRoles(userId);
    }

    @Override
    public boolean removeRole(Long userId, Long roleId) {
        int rows = userRoleMapper.delete(
                new LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getUserId, userId)
                        .eq(UserRole::getRoleId, roleId));
        return rows > 0;
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
