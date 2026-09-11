package com.blog.services.userroles.models.vo;

import java.io.Serializable;
import java.util.List;

/**
 * 给用户分配角色请求VO
 */
public class AssignRolesVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID列表
     */
    private List<Long> roleIds;

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }
}
