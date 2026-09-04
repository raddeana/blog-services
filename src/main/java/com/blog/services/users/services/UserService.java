package com.blog.services.users.services;

import com.blog.services.users.models.dto.UserDTO;
import com.blog.services.users.models.vo.UserVO;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 创建用户
     *
     * @param vo 创建用户请求
     * @return 创建后的用户信息
     */
    UserDTO createUser(UserVO vo);

    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    UserDTO getUserById(Long id);

    /**
     * 查询所有用户
     *
     * @return 用户列表
     */
    List<UserDTO> listUsers();

    /**
     * 更新用户信息
     *
     * @param id  用户ID
     * @param vo  更新用户请求
     * @return 更新后的用户信息
     */
    UserDTO updateUser(Long id, UserVO vo);

    /**
     * 删除用户
     *
     * @param id 用户ID
     */
    void deleteUser(Long id);
}
