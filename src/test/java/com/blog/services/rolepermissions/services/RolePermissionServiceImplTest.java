package com.blog.services.rolepermissions.services;

import com.blog.services.permissions.mappers.PermissionMapper;
import com.blog.services.permissions.models.Permission;
import com.blog.services.permissions.models.dto.PermissionDTO;
import com.blog.services.roles.models.dto.RoleDTO;
import com.blog.services.roles.services.RoleService;
import com.blog.services.rolepermissions.mappers.RolePermissionMapper;
import com.blog.services.rolepermissions.models.RolePermission;
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
 * 角色-权限关联服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class RolePermissionServiceImplTest {

    @Mock
    private RolePermissionMapper rolePermissionMapper;

    @Mock
    private PermissionMapper permissionMapper;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RolePermissionServiceImpl rolePermissionService;

    @Test
    void getRolePermissions_empty() {
        when(rolePermissionMapper.selectList(any())).thenReturn(Collections.emptyList());

        List<PermissionDTO> result = rolePermissionService.getRolePermissions(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getRolePermissions_hasData() {
        RolePermission rel1 = new RolePermission();
        rel1.setRoleId(1L);
        rel1.setPermissionId(10L);
        RolePermission rel2 = new RolePermission();
        rel2.setRoleId(1L);
        rel2.setPermissionId(20L);

        Permission perm1 = new Permission();
        perm1.setId(10L);
        perm1.setPermissionName("文章查看");
        Permission perm2 = new Permission();
        perm2.setId(20L);
        perm2.setPermissionName("文章发布");

        when(rolePermissionMapper.selectList(any())).thenReturn(Arrays.asList(rel1, rel2));
        when(permissionMapper.selectById(10L)).thenReturn(perm1);
        when(permissionMapper.selectById(20L)).thenReturn(perm2);

        List<PermissionDTO> result = rolePermissionService.getRolePermissions(1L);

        assertEquals(2, result.size());
        assertEquals("文章查看", result.get(0).getPermissionName());
        assertEquals("文章发布", result.get(1).getPermissionName());
    }

    @Test
    void getRolePermissions_permissionDeleted() {
        RolePermission rel = new RolePermission();
        rel.setRoleId(1L);
        rel.setPermissionId(99L);

        when(rolePermissionMapper.selectList(any())).thenReturn(Collections.singletonList(rel));
        when(permissionMapper.selectById(99L)).thenReturn(null);

        List<PermissionDTO> result = rolePermissionService.getRolePermissions(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void assignPermissions_success() {
        RoleDTO role = new RoleDTO();
        role.setId(1L);
        when(roleService.getRoleById(1L)).thenReturn(role);

        Permission perm1 = new Permission();
        perm1.setId(10L);
        perm1.setPermissionName("查看");
        Permission perm2 = new Permission();
        perm2.setId(20L);
        perm2.setPermissionName("发布");

        when(permissionMapper.selectById(10L)).thenReturn(perm1);
        when(permissionMapper.selectById(20L)).thenReturn(perm2);
        when(rolePermissionMapper.selectCount(any())).thenReturn(0L);
        when(rolePermissionMapper.insert(any(RolePermission.class))).thenReturn(1);

        RolePermission r1 = new RolePermission();
        r1.setPermissionId(10L);
        RolePermission r2 = new RolePermission();
        r2.setPermissionId(20L);
        when(rolePermissionMapper.selectList(any())).thenReturn(Arrays.asList(r1, r2));

        List<PermissionDTO> result = rolePermissionService.assignPermissions(1L, Arrays.asList(10L, 20L));

        assertEquals(2, result.size());
        verify(rolePermissionMapper, times(2)).insert(any(RolePermission.class));
    }

    @Test
    void assignPermissions_roleNotFound() {
        when(roleService.getRoleById(999L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () ->
                rolePermissionService.assignPermissions(999L, Arrays.asList(10L)));
    }

    @Test
    void assignPermissions_permissionNotFound_filteredOut() {
        RoleDTO role = new RoleDTO();
        role.setId(1L);
        when(roleService.getRoleById(1L)).thenReturn(role);

        when(permissionMapper.selectById(999L)).thenReturn(null);
        when(rolePermissionMapper.selectList(any())).thenReturn(Collections.emptyList());

        List<PermissionDTO> result = rolePermissionService.assignPermissions(1L, Collections.singletonList(999L));

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(rolePermissionMapper, never()).insert(any(RolePermission.class));
    }

    @Test
    void assignPermissions_alreadyAssociated_skipped() {
        RoleDTO role = new RoleDTO();
        role.setId(1L);
        when(roleService.getRoleById(1L)).thenReturn(role);

        Permission perm = new Permission();
        perm.setId(10L);
        perm.setPermissionName("查看");
        when(permissionMapper.selectById(10L)).thenReturn(perm);
        when(rolePermissionMapper.selectCount(any())).thenReturn(1L);

        RolePermission rel = new RolePermission();
        rel.setPermissionId(10L);
        when(rolePermissionMapper.selectList(any())).thenReturn(Collections.singletonList(rel));

        List<PermissionDTO> result = rolePermissionService.assignPermissions(1L, Collections.singletonList(10L));

        assertEquals(1, result.size());
        verify(rolePermissionMapper, never()).insert(any(RolePermission.class));
    }

    @Test
    void removePermission_success() {
        when(rolePermissionMapper.delete(any())).thenReturn(1);

        boolean removed = rolePermissionService.removePermission(1L, 10L);

        assertTrue(removed);
    }

    @Test
    void removePermission_notAssociated() {
        when(rolePermissionMapper.delete(any())).thenReturn(0);

        boolean removed = rolePermissionService.removePermission(1L, 10L);

        assertFalse(removed);
    }
}
