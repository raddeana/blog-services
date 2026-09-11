-- ============================================================
-- Blog User Services - RBAC 建表脚本（幂等，可重复执行）
-- 应用启动时由 Spring Boot 自动执行（spring.sql.init.mode=always）
--
-- 首次启动前需确保 MySQL 中已存在 blog_db 库：
--   CREATE DATABASE IF NOT EXISTS blog_db DEFAULT CHARACTER SET utf8mb4;
-- 数据源 url 已指向 blog_db，此脚本仅负责建表，不建库
-- ============================================================

-- ----------------------------
-- 1. 用户表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`    VARCHAR(64)  NOT NULL COMMENT '用户名',
    `password`    VARCHAR(128) NOT NULL COMMENT '密码',
    `email`       VARCHAR(128)          COMMENT '邮箱',
    `nickname`    VARCHAR(64)           COMMENT '昵称',
    `avatar`      VARCHAR(255)          COMMENT '头像URL',
    `status`      INT           NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    `create_time` BIGINT                 COMMENT '创建时间（毫秒）',
    `update_time` BIGINT                 COMMENT '更新时间（毫秒）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- 2. 角色表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `role` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `role_name`   VARCHAR(64)  NOT NULL COMMENT '角色名称',
    `role_code`   VARCHAR(64)  NOT NULL COMMENT '角色编码',
    `description` VARCHAR(255)          COMMENT '描述',
    `status`      INT           NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    `create_time` BIGINT                 COMMENT '创建时间（毫秒）',
    `update_time` BIGINT                 COMMENT '更新时间（毫秒）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ----------------------------
-- 3. 权限表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `permission` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '权限ID',
    `permission_name` VARCHAR(64)  NOT NULL COMMENT '权限名称',
    `permission_code` VARCHAR(64)  NOT NULL COMMENT '权限编码',
    `resource_type`   VARCHAR(32)           COMMENT '资源类型',
    `resource_path`   VARCHAR(255)          COMMENT '资源路径',
    `description`     VARCHAR(255)          COMMENT '描述',
    `status`          INT           NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    `create_time`     BIGINT                 COMMENT '创建时间（毫秒）',
    `update_time`     BIGINT                 COMMENT '更新时间（毫秒）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_permission_code` (`permission_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- ----------------------------
-- 4. 用户-角色关联表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `user_role` (
    `id`      BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户-角色关联表';

-- ----------------------------
-- 5. 角色-权限关联表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `role_permission` (
    `id`            BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `role_id`       BIGINT NOT NULL COMMENT '角色ID',
    `permission_id` BIGINT NOT NULL COMMENT '权限ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
    KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色-权限关联表';
