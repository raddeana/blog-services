package com.blog.services.userroles.controllers;

import com.blog.services.common.Result;
import com.blog.services.roles.models.dto.RoleDTO;
import com.blog.services.userroles.models.vo.AssignRolesVO;
import com.blog.services.userroles.services.UserRoleService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 用户-角色关联控制器
 *
 * RESTful API设计：
 * GET    /api/users/{userId}/roles            查询用户的角色列表
 * POST   /api/users/{userId}/roles            给用户分配角色（批量）
 * DELETE /api/users/{userId}/roles/{roleId}   移除用户的单个角色
 */
@RestController
@RequestMapping("/api/users")
public class UserRoleController {

    @Resource
    private UserRoleService userRoleService;

    /**
     * 查询用户的角色列表
     */
    @GetMapping("/{userId}/roles")
    public Result<List<RoleDTO>> getUserRoles(@PathVariable("userId") Long userId) {
        if (userId == null || userId <= 0) {
            return Result.badRequest("用户ID无效");
        }
        List<RoleDTO> roles = userRoleService.getUserRoles(userId);
        return Result.success(roles);
    }

    /**
     * 给用户分配角色（批量）
     */
    @PostMapping("/{userId}/roles")
    public Result<List<RoleDTO>> assignRoles(
            @PathVariable("userId") Long userId,
            @RequestBody AssignRolesVO vo) {
        if (userId == null || userId <= 0) {
            return Result.badRequest("用户ID无效");
        }
        if (vo == null || vo.getRoleIds() == null || vo.getRoleIds().isEmpty()) {
            return Result.badRequest("角色ID列表不能为空");
        }
        try {
            List<RoleDTO> roles = userRoleService.assignRoles(userId, vo.getRoleIds());
            return Result.success("角色分配成功", roles);
        } catch (IllegalArgumentException e) {
            return Result.notFound(e.getMessage());
        } catch (Exception e) {
            return Result.error("角色分配失败：" + e.getMessage());
        }
    }

    /**
     * 移除用户的单个角色
     */
    @DeleteMapping("/{userId}/roles/{roleId}")
    public Result<Void> removeRole(
            @PathVariable("userId") Long userId,
            @PathVariable("roleId") Long roleId) {
        if (userId == null || userId <= 0) {
            return Result.badRequest("用户ID无效");
        }
        if (roleId == null || roleId <= 0) {
            return Result.badRequest("角色ID无效");
        }
        boolean removed = userRoleService.removeRole(userId, roleId);
        if (!removed) {
            return Result.notFound("用户未关联该角色，userId: " + userId + ", roleId: " + roleId);
        }
        return Result.success("角色移除成功", null);
    }
}
