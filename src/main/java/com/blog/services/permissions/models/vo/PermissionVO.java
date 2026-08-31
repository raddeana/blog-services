package com.blog.services.permissions.models.vo;

import java.io.Serializable;

/**
 * 创建/更新权限请求VO
 */
public class PermissionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String permissionName;
    private String permissionCode;
    private String resourceType;
    private String resourcePath;
    private String description;

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public String getPermissionCode() {
        return permissionCode;
    }

    public void setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
