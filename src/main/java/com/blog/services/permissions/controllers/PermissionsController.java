package com.blog.services.permissions.controllers;

import com.blog.services.common.Result;
import com.blog.services.permissions.models.dto.PermissionDTO;
import com.blog.services.permissions.models.vo.PermissionVO;
import com.blog.services.permissions.services.PermissionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 权限管理控制器
 *
 * RESTful API设计：
 * GET    /api/permissions          查询权限列表
 * GET    /api/permissions/{id}     查询单个权限
 * POST   /api/permissions          创建权限
 * PUT    /api/permissions/{id}     更新权限
 * DELETE /api/permissions/{id}     删除权限
 */
@RestController
@RequestMapping("/api/permissions")
public class PermissionsController {

    @Resource
    private PermissionService permissionService;

    /**
     * 查询权限列表
     */
    @GetMapping
    public Result<List<PermissionDTO>> listPermissions() {
        List<PermissionDTO> permissions = permissionService.listPermissions();
        return Result.success(permissions);
    }

    /**
     * 根据ID查询权限
     */
    @GetMapping("/{id}")
    public Result<PermissionDTO> getPermissionById(@PathVariable("id") Long id) {
        if (id == null || id <= 0) {
            return Result.badRequest("权限ID无效");
        }
        PermissionDTO permission = permissionService.getPermissionById(id);
        if (permission == null) {
            return Result.notFound("权限不存在，ID: " + id);
        }
        return Result.success(permission);
    }

    /**
     * 创建权限
     */
    @PostMapping
    public Result<PermissionDTO> createPermission(@RequestBody PermissionVO vo) {
        if (vo == null) {
            return Result.badRequest("请求体不能为空");
        }
        if (StringUtils.isBlank(vo.getPermissionName())) {
            return Result.badRequest("权限名称不能为空");
        }
        if (StringUtils.isBlank(vo.getPermissionCode())) {
            return Result.badRequest("权限编码不能为空");
        }
        try {
            PermissionDTO permission = permissionService.createPermission(vo);
            return Result.success("权限创建成功", permission);
        } catch (Exception e) {
            return Result.error("权限创建失败：" + e.getMessage());
        }
    }

    /**
     * 更新权限
     */
    @PutMapping("/{id}")
    public Result<PermissionDTO> updatePermission(
            @PathVariable("id") Long id,
            @RequestBody PermissionVO vo) {
        if (id == null || id <= 0) {
            return Result.badRequest("权限ID无效");
        }
        if (vo == null) {
            return Result.badRequest("请求体不能为空");
        }
        try {
            PermissionDTO permission = permissionService.updatePermission(id, vo);
            if (permission == null) {
                return Result.notFound("权限不存在，ID: " + id);
            }
            return Result.success("权限更新成功", permission);
        } catch (Exception e) {
            return Result.error("权限更新失败：" + e.getMessage());
        }
    }

    /**
     * 删除权限
     */
    @DeleteMapping("/{id}")
    public Result<Void> deletePermission(@PathVariable("id") Long id) {
        if (id == null || id <= 0) {
            return Result.badRequest("权限ID无效");
        }
        PermissionDTO existing = permissionService.getPermissionById(id);
        if (existing == null) {
            return Result.notFound("权限不存在，ID: " + id);
        }
        try {
            permissionService.deletePermission(id);
            return Result.success("权限删除成功", null);
        } catch (Exception e) {
            return Result.error("权限删除失败：" + e.getMessage());
        }
    }
}
