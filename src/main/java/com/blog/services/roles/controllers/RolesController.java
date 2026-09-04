package com.blog.services.roles.controllers;

import com.blog.services.common.Result;
import com.blog.services.roles.models.dto.RoleDTO;
import com.blog.services.roles.models.vo.CreateRoleVO;
import com.blog.services.roles.services.RoleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 角色管理控制器
 *
 * RESTful API设计：
 * GET    /api/roles          查询角色列表
 * GET    /api/roles/{id}     查询单个角色
 * POST   /api/roles          创建角色
 * PUT    /api/roles/{id}     更新角色
 * DELETE /api/roles/{id}     删除角色
 */
@RestController
@RequestMapping("/api/roles")
public class RolesController {

    @Resource
    private RoleService roleService;

    /**
     * 查询角色列表
     */
    @GetMapping
    public Result<List<RoleDTO>> listRoles() {
        List<RoleDTO> roles = roleService.listRoles();
        return Result.success(roles);
    }

    /**
     * 根据ID查询角色
     */
    @GetMapping("/{id}")
    public Result<RoleDTO> getRoleById(@PathVariable("id") Long id) {
        if (id == null || id <= 0) {
            return Result.badRequest("角色ID无效");
        }
        RoleDTO role = roleService.getRoleById(id);
        if (role == null) {
            return Result.notFound("角色不存在，ID: " + id);
        }
        return Result.success(role);
    }

    /**
     * 创建角色
     */
    @PostMapping
    public Result<RoleDTO> createRole(@RequestBody CreateRoleVO vo) {
        if (vo == null) {
            return Result.badRequest("请求体不能为空");
        }
        if (StringUtils.isBlank(vo.getRoleName())) {
            return Result.badRequest("角色名称不能为空");
        }
        if (StringUtils.isBlank(vo.getRoleCode())) {
            return Result.badRequest("角色编码不能为空");
        }
        try {
            RoleDTO role = roleService.createRole(vo);
            return Result.success("角色创建成功", role);
        } catch (Exception e) {
            return Result.error("角色创建失败：" + e.getMessage());
        }
    }

    /**
     * 更新角色
     */
    @PutMapping("/{id}")
    public Result<RoleDTO> updateRole(
            @PathVariable("id") Long id,
            @RequestBody CreateRoleVO vo) {
        if (id == null || id <= 0) {
            return Result.badRequest("角色ID无效");
        }
        if (vo == null) {
            return Result.badRequest("请求体不能为空");
        }
        try {
            RoleDTO role = roleService.updateRole(id, vo);
            if (role == null) {
                return Result.notFound("角色不存在，ID: " + id);
            }
            return Result.success("角色更新成功", role);
        } catch (Exception e) {
            return Result.error("角色更新失败：" + e.getMessage());
        }
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteRole(@PathVariable("id") Long id) {
        if (id == null || id <= 0) {
            return Result.badRequest("角色ID无效");
        }
        RoleDTO existing = roleService.getRoleById(id);
        if (existing == null) {
            return Result.notFound("角色不存在，ID: " + id);
        }
        try {
            roleService.deleteRole(id);
            return Result.success("角色删除成功", null);
        } catch (Exception e) {
            return Result.error("角色删除失败：" + e.getMessage());
        }
    }
}
