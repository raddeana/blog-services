package com.blog.services.users.services;

import com.blog.services.users.models.User;
import com.blog.services.users.models.dto.UserDTO;
import com.blog.services.users.models.vo.UserVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    /**
     * 模拟存储（后续替换为MyBatis Mapper）
     */
    private final Map<Long, User> userRepository = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public UserDTO createUser(UserVO vo) {
        User user = new User();
        user.setId(idGenerator.getAndIncrement());
        user.setUsername(vo.getUsername());
        user.setPassword(vo.getPassword());
        user.setEmail(vo.getEmail());
        user.setNickname(vo.getNickname());
        user.setAvatar(vo.getAvatar());
        user.setStatus(1);
        user.setCreateTime(System.currentTimeMillis());
        user.setUpdateTime(System.currentTimeMillis());
        userRepository.put(user.getId(), user);
        return convertToDTO(user);
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.get(id);
        if (user == null) {
            return null;
        }
        return convertToDTO(user);
    }

    @Override
    public List<UserDTO> listUsers() {
        List<UserDTO> result = new ArrayList<>();
        for (User user : userRepository.values()) {
            result.add(convertToDTO(user));
        }
        return result;
    }

    @Override
    public UserDTO updateUser(Long id, UserVO vo) {
        User user = userRepository.get(id);
        if (user == null) {
            return null;
        }
        if (vo.getUsername() != null) {
            user.setUsername(vo.getUsername());
        }
        if (vo.getPassword() != null) {
            user.setPassword(vo.getPassword());
        }
        if (vo.getEmail() != null) {
            user.setEmail(vo.getEmail());
        }
        if (vo.getNickname() != null) {
            user.setNickname(vo.getNickname());
        }
        if (vo.getAvatar() != null) {
            user.setAvatar(vo.getAvatar());
        }
        user.setUpdateTime(System.currentTimeMillis());
        return convertToDTO(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.remove(id);
    }

    /**
     * 实体转DTO
     */
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setNickname(user.getNickname());
        dto.setAvatar(user.getAvatar());
        dto.setStatus(user.getStatus());
        dto.setCreateTime(user.getCreateTime());
        dto.setUpdateTime(user.getUpdateTime());
        return dto;
    }
}
