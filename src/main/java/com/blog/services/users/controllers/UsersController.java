package com.blog.services.users.controllers;

import com.blog.services.common.Result;
import com.blog.services.users.models.dto.UserDTO;
import com.blog.services.users.models.vo.UserVO;
import com.blog.services.users.services.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 用户管理控制器
 *
 * RESTful API设计：
 * GET    /api/users          查询用户列表
 * GET    /api/users/{id}     查询单个用户
 * POST   /api/users          创建用户
 * PUT    /api/users/{id}     更新用户
 * DELETE /api/users/{id}     删除用户
 */
@RestController
@RequestMapping("/api/users")
public class UsersController {

    @Resource
    private UserService userService;

    /**
     * 查询用户列表
     */
    @GetMapping
    public Result<List<UserDTO>> listUsers() {
        List<UserDTO> users = userService.listUsers();
        return Result.success(users);
    }

    /**
     * 根据ID查询用户
     */
    @GetMapping("/{id}")
    public Result<UserDTO> getUserById(@PathVariable("id") Long id) {
        if (id == null || id <= 0) {
            return Result.badRequest("用户ID无效");
        }
        UserDTO user = userService.getUserById(id);
        if (user == null) {
            return Result.notFound("用户不存在，ID: " + id);
        }
        return Result.success(user);
    }

    /**
     * 创建用户
     */
    @PostMapping
    public Result<UserDTO> createUser(@RequestBody UserVO vo) {
        // 参数校验
        if (vo == null) {
            return Result.badRequest("请求体不能为空");
        }
        if (StringUtils.isBlank(vo.getUsername())) {
            return Result.badRequest("用户名不能为空");
        }
        if (StringUtils.isBlank(vo.getPassword())) {
            return Result.badRequest("密码不能为空");
        }
        if (vo.getPassword().length() < 6) {
            return Result.badRequest("密码长度不能少于6位");
        }
        if (StringUtils.isBlank(vo.getEmail())) {
            return Result.badRequest("邮箱不能为空");
        }
        // 简单邮箱格式校验
        if (!vo.getEmail().contains("@")) {
            return Result.badRequest("邮箱格式不正确");
        }
        try {
            UserDTO user = userService.createUser(vo);
            return Result.success("用户创建成功", user);
        } catch (Exception e) {
            return Result.error("用户创建失败：" + e.getMessage());
        }
    }

    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    public Result<UserDTO> updateUser(
            @PathVariable("id") Long id,
            @RequestBody UserVO vo) {
        if (id == null || id <= 0) {
            return Result.badRequest("用户ID无效");
        }
        if (vo == null) {
            return Result.badRequest("请求体不能为空");
        }
        // 邮箱格式校验（如果传了邮箱）
        if (StringUtils.isNotBlank(vo.getEmail()) && !vo.getEmail().contains("@")) {
            return Result.badRequest("邮箱格式不正确");
        }
        // 密码长度校验（如果传了密码）
        if (StringUtils.isNotBlank(vo.getPassword()) && vo.getPassword().length() < 6) {
            return Result.badRequest("密码长度不能少于6位");
        }
        try {
            UserDTO user = userService.updateUser(id, vo);
            if (user == null) {
                return Result.notFound("用户不存在，ID: " + id);
            }
            return Result.success("用户更新成功", user);
        } catch (Exception e) {
            return Result.error("用户更新失败：" + e.getMessage());
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable("id") Long id) {
        if (id == null || id <= 0) {
            return Result.badRequest("用户ID无效");
        }
        UserDTO existing = userService.getUserById(id);
        if (existing == null) {
            return Result.notFound("用户不存在，ID: " + id);
        }
        try {
            userService.deleteUser(id);
            return Result.success("用户删除成功", null);
        } catch (Exception e) {
            return Result.error("用户删除失败：" + e.getMessage());
        }
    }
}
