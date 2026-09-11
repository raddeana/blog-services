package com.blog.services.roles.services;

import com.blog.services.roles.mappers.RoleMapper;
import com.blog.services.roles.models.Role;
import com.blog.services.roles.models.dto.RoleDTO;
import com.blog.services.roles.models.vo.CreateRoleVO;
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
 * 角色服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleServiceImpl roleService;

    @Test
    void createRole_success() {
        CreateRoleVO vo = new CreateRoleVO();
        vo.setRoleName("管理员");
        vo.setRoleCode("ROLE_ADMIN");
        vo.setDescription("系统管理员");

        doAnswer(invocation -> {
            Role role = invocation.getArgument(0);
            role.setId(1L);
            return 1;
        }).when(roleMapper).insert(any(Role.class));

        RoleDTO result = roleService.createRole(vo);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("管理员", result.getRoleName());
        assertEquals("ROLE_ADMIN", result.getRoleCode());
        assertEquals("系统管理员", result.getDescription());
        assertEquals(1, result.getStatus());
        assertNotNull(result.getCreateTime());
        assertNotNull(result.getUpdateTime());
        verify(roleMapper).insert(any(Role.class));
    }

    @Test
    void getRoleById_found() {
        Role role = new Role();
        role.setId(1L);
        role.setRoleName("管理员");
        role.setRoleCode("ROLE_ADMIN");
        role.setStatus(1);
        when(roleMapper.selectById(1L)).thenReturn(role);

        RoleDTO result = roleService.getRoleById(1L);

        assertNotNull(result);
        assertEquals("管理员", result.getRoleName());
    }

    @Test
    void getRoleById_notFound() {
        when(roleMapper.selectById(999L)).thenReturn(null);

        RoleDTO result = roleService.getRoleById(999L);

        assertNull(result);
    }

    @Test
    void listRoles_empty() {
        when(roleMapper.selectList(null)).thenReturn(Collections.emptyList());

        List<RoleDTO> result = roleService.listRoles();

        assertTrue(result.isEmpty());
    }

    @Test
    void listRoles_hasData() {
        Role r1 = new Role();
        r1.setId(1L);
        r1.setRoleName("管理员");
        Role r2 = new Role();
        r2.setId(2L);
        r2.setRoleName("编辑");
        when(roleMapper.selectList(null)).thenReturn(Arrays.asList(r1, r2));

        List<RoleDTO> result = roleService.listRoles();

        assertEquals(2, result.size());
        assertEquals("管理员", result.get(0).getRoleName());
        assertEquals("编辑", result.get(1).getRoleName());
    }

    @Test
    void updateRole_success() {
        Role existing = new Role();
        existing.setId(1L);
        existing.setRoleName("旧名称");
        existing.setRoleCode("ROLE_OLD");
        when(roleMapper.selectById(1L)).thenReturn(existing);

        CreateRoleVO vo = new CreateRoleVO();
        vo.setRoleName("新名称");
        vo.setRoleCode("ROLE_NEW");

        RoleDTO result = roleService.updateRole(1L, vo);

        assertNotNull(result);
        assertEquals("新名称", result.getRoleName());
        assertEquals("ROLE_NEW", result.getRoleCode());
        verify(roleMapper).updateById(any(Role.class));
    }

    @Test
    void updateRole_partialUpdate() {
        Role existing = new Role();
        existing.setId(1L);
        existing.setRoleName("keepname");
        existing.setRoleCode("ROLE_KEEP");
        when(roleMapper.selectById(1L)).thenReturn(existing);

        CreateRoleVO vo = new CreateRoleVO();
        vo.setDescription("新描述");

        RoleDTO result = roleService.updateRole(1L, vo);

        assertNotNull(result);
        assertEquals("keepname", result.getRoleName());
        assertEquals("ROLE_KEEP", result.getRoleCode());
        assertEquals("新描述", result.getDescription());
    }

    @Test
    void updateRole_notFound() {
        when(roleMapper.selectById(999L)).thenReturn(null);

        CreateRoleVO vo = new CreateRoleVO();
        vo.setRoleName("name");

        RoleDTO result = roleService.updateRole(999L, vo);

        assertNull(result);
        verify(roleMapper, never()).updateById(any(Role.class));
    }

    @Test
    void deleteRole() {
        roleService.deleteRole(1L);
        verify(roleMapper).deleteById(1L);
    }
}
