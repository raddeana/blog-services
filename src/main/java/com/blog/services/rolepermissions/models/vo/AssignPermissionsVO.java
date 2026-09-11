package com.blog.services.rolepermissions.models.vo;

import java.io.Serializable;
import java.util.List;

/**
 * 给角色分配权限请求VO
 */
public class AssignPermissionsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 权限ID列表
     */
    private List<Long> permissionIds;

    public List<Long> getPermissionIds() {
        return permissionIds;
    }

    public void setPermissionIds(List<Long> permissionIds) {
        this.permissionIds = permissionIds;
    }
}
