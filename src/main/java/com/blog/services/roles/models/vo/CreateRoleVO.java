package com.blog.services.roles.models.vo;

import java.io.Serializable;

/**
 * 创建/更新角色请求VO
 */
public class CreateRoleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String roleName;
    private String roleCode;
    private String description;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
