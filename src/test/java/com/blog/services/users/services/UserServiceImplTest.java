package com.blog.services.users.services;

import com.blog.services.users.mappers.UserMapper;
import com.blog.services.users.models.User;
import com.blog.services.users.models.dto.UserDTO;
import com.blog.services.users.models.vo.UserVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 用户服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_success() {
        UserVO vo = new UserVO();
        vo.setUsername("testuser");
        vo.setPassword("123456");
        vo.setEmail("test@blog.com");
        vo.setNickname("测试用户");

        // insert 后 MyBatis Plus 会回填 id
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return 1;
        }).when(userMapper).insert(any(User.class));

        UserDTO result = userService.createUser(vo);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals("test@blog.com", result.getEmail());
        assertEquals("测试用户", result.getNickname());
        assertEquals(1, result.getStatus());
        assertNotNull(result.getCreateTime());
        assertNotNull(result.getUpdateTime());
        verify(userMapper, times(1)).insert(any(User.class));
    }

    @Test
    void getUserById_found() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@blog.com");
        user.setNickname("测试用户");
        user.setStatus(1);
        when(userMapper.selectById(1L)).thenReturn(user);

        UserDTO result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void getUserById_notFound() {
        when(userMapper.selectById(999L)).thenReturn(null);

        UserDTO result = userService.getUserById(999L);

        assertNull(result);
    }

    @Test
    void listUsers_empty() {
        when(userMapper.selectList(null)).thenReturn(Collections.emptyList());

        List<UserDTO> result = userService.listUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void listUsers_hasData() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        when(userMapper.selectList(null)).thenReturn(Arrays.asList(user1, user2));

        List<UserDTO> result = userService.listUsers();

        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getUsername());
        assertEquals("user2", result.get(1).getUsername());
    }

    @Test
    void updateUser_success() {
        User existing = new User();
        existing.setId(1L);
        existing.setUsername("oldname");
        existing.setEmail("old@blog.com");
        when(userMapper.selectById(1L)).thenReturn(existing);

        UserVO vo = new UserVO();
        vo.setUsername("newname");
        vo.setEmail("new@blog.com");

        UserDTO result = userService.updateUser(1L, vo);

        assertNotNull(result);
        assertEquals("newname", result.getUsername());
        assertEquals("new@blog.com", result.getEmail());
        verify(userMapper).updateById(any(User.class));
    }

    @Test
    void updateUser_partialUpdate() {
        User existing = new User();
        existing.setId(1L);
        existing.setUsername("keepname");
        existing.setEmail("old@blog.com");
        when(userMapper.selectById(1L)).thenReturn(existing);

        UserVO vo = new UserVO();
        // 只更新邮箱，username 不传
        vo.setEmail("new@blog.com");

        UserDTO result = userService.updateUser(1L, vo);

        assertNotNull(result);
        assertEquals("keepname", result.getUsername());
        assertEquals("new@blog.com", result.getEmail());
    }

    @Test
    void updateUser_notFound() {
        when(userMapper.selectById(999L)).thenReturn(null);

        UserVO vo = new UserVO();
        vo.setUsername("name");

        UserDTO result = userService.updateUser(999L, vo);

        assertNull(result);
        verify(userMapper, never()).updateById(any(User.class));
    }

    @Test
    void deleteUser() {
        userService.deleteUser(1L);
        verify(userMapper).deleteById(1L);
    }
}
