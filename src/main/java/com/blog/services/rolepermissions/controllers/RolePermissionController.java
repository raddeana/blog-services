package com.blog.services.rolepermissions.controllers;

import com.blog.services.common.Result;
import com.blog.services.permissions.models.dto.PermissionDTO;
import com.blog.services.rolepermissions.models.vo.AssignPermissionsVO;
import com.blog.services.rolepermissions.services.RolePermissionService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 角色-权限关联控制器
 *
 * RESTful API设计：
 * GET    /api/roles/{roleId}/permissions                 查询角色的权限列表
 * POST   /api/roles/{roleId}/permissions                 给角色分配权限（批量）
 * DELETE /api/roles/{roleId}/permissions/{permissionId}  移除角色的单个权限
 */
@RestController
@RequestMapping("/api/roles")
public class RolePermissionController {

    @Resource
    private RolePermissionService rolePermissionService;

    /**
     * 查询角色的权限列表
     */
    @GetMapping("/{roleId}/permissions")
    public Result<List<PermissionDTO>> getRolePermissions(@PathVariable("roleId") Long roleId) {
        if (roleId == null || roleId <= 0) {
            return Result.badRequest("角色ID无效");
        }
        List<PermissionDTO> permissions = rolePermissionService.getRolePermissions(roleId);
        return Result.success(permissions);
    }

    /**
     * 给角色分配权限（批量）
     */
    @PostMapping("/{roleId}/permissions")
    public Result<List<PermissionDTO>> assignPermissions(
            @PathVariable("roleId") Long roleId,
            @RequestBody AssignPermissionsVO vo) {
        if (roleId == null || roleId <= 0) {
            return Result.badRequest("角色ID无效");
        }
        if (vo == null || vo.getPermissionIds() == null || vo.getPermissionIds().isEmpty()) {
            return Result.badRequest("权限ID列表不能为空");
        }
        try {
            List<PermissionDTO> permissions = rolePermissionService.assignPermissions(roleId, vo.getPermissionIds());
            return Result.success("权限分配成功", permissions);
        } catch (IllegalArgumentException e) {
            return Result.notFound(e.getMessage());
        } catch (Exception e) {
            return Result.error("权限分配失败：" + e.getMessage());
        }
    }

    /**
     * 移除角色的单个权限
     */
    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    public Result<Void> removePermission(
            @PathVariable("roleId") Long roleId,
            @PathVariable("permissionId") Long permissionId) {
        if (roleId == null || roleId <= 0) {
            return Result.badRequest("角色ID无效");
        }
        if (permissionId == null || permissionId <= 0) {
            return Result.badRequest("权限ID无效");
        }
        boolean removed = rolePermissionService.removePermission(roleId, permissionId);
        if (!removed) {
            return Result.notFound("角色未关联该权限，roleId: " + roleId + ", permissionId: " + permissionId);
        }
        return Result.success("权限移除成功", null);
    }
}
