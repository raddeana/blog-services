package com.blog.services.permissions.services;

import com.blog.services.permissions.mappers.PermissionMapper;
import com.blog.services.permissions.models.Permission;
import com.blog.services.permissions.models.dto.PermissionDTO;
import com.blog.services.permissions.models.vo.PermissionVO;
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
 * 权限服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class PermissionServiceImplTest {

    @Mock
    private PermissionMapper permissionMapper;

    @InjectMocks
    private PermissionServiceImpl permissionService;

    @Test
    void createPermission_success() {
        PermissionVO vo = new PermissionVO();
        vo.setPermissionName("文章查看");
        vo.setPermissionCode("article:view");
        vo.setResourceType("API");
        vo.setResourcePath("/api/articles/**");
        vo.setDescription("查看文章");

        doAnswer(invocation -> {
            Permission p = invocation.getArgument(0);
            p.setId(1L);
            return 1;
        }).when(permissionMapper).insert(any(Permission.class));

        PermissionDTO result = permissionService.createPermission(vo);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("文章查看", result.getPermissionName());
        assertEquals("article:view", result.getPermissionCode());
        assertEquals("API", result.getResourceType());
        assertEquals("/api/articles/**", result.getResourcePath());
        assertEquals(1, result.getStatus());
        verify(permissionMapper).insert(any(Permission.class));
    }

    @Test
    void getPermissionById_found() {
        Permission p = new Permission();
        p.setId(1L);
        p.setPermissionName("文章查看");
        p.setPermissionCode("article:view");
        p.setStatus(1);
        when(permissionMapper.selectById(1L)).thenReturn(p);

        PermissionDTO result = permissionService.getPermissionById(1L);

        assertNotNull(result);
        assertEquals("文章查看", result.getPermissionName());
    }

    @Test
    void getPermissionById_notFound() {
        when(permissionMapper.selectById(999L)).thenReturn(null);

        PermissionDTO result = permissionService.getPermissionById(999L);

        assertNull(result);
    }

    @Test
    void listPermissions_empty() {
        when(permissionMapper.selectList(null)).thenReturn(Collections.emptyList());

        List<PermissionDTO> result = permissionService.listPermissions();

        assertTrue(result.isEmpty());
    }

    @Test
    void listPermissions_hasData() {
        Permission p1 = new Permission();
        p1.setId(1L);
        p1.setPermissionName("查看");
        Permission p2 = new Permission();
        p2.setId(2L);
        p2.setPermissionName("发布");
        when(permissionMapper.selectList(null)).thenReturn(Arrays.asList(p1, p2));

        List<PermissionDTO> result = permissionService.listPermissions();

        assertEquals(2, result.size());
    }

    @Test
    void updatePermission_success() {
        Permission existing = new Permission();
        existing.setId(1L);
        existing.setPermissionName("旧名称");
        when(permissionMapper.selectById(1L)).thenReturn(existing);

        PermissionVO vo = new PermissionVO();
        vo.setPermissionName("新名称");

        PermissionDTO result = permissionService.updatePermission(1L, vo);

        assertNotNull(result);
        assertEquals("新名称", result.getPermissionName());
        verify(permissionMapper).updateById(any(Permission.class));
    }

    @Test
    void updatePermission_notFound() {
        when(permissionMapper.selectById(999L)).thenReturn(null);

        PermissionVO vo = new PermissionVO();
        vo.setPermissionName("name");

        PermissionDTO result = permissionService.updatePermission(999L, vo);

        assertNull(result);
        verify(permissionMapper, never()).updateById(any(Permission.class));
    }

    @Test
    void deletePermission() {
        permissionService.deletePermission(1L);
        verify(permissionMapper).deleteById(1L);
    }
}
