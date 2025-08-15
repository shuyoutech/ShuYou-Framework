package com.shuyoutech.common.core.constant;

/**
 * 权限相关通用常量
 *
 * @author YangChao
 * @date 2025-08-15 9:30
 **/
public interface AuthConstants {

    /**
     * 登录用户
     */
    String LOGIN_USER = "login_user";

    /**
     * 手机号字段
     */
    String PHONE = "phone";

    /**
     * 邮箱字段
     */
    String EMAIL = "email";

    /**
     * 租管角色标识
     */
    String ROLE_ADMINISTRATOR = "administrator";

    /**
     * 超管角色标识
     */
    String ROLE_ADMIN = "admin";

    /**
     * 超管菜单标识
     */
    String PERMISSION_ADMIN = "*:*:*";

    /**
     * 授权信息
     */
    String AUTHORIZATION_HEADER = "authorization";

    /**
     * {bcrypt} 加密的特征码
     */
    String BCRYPT = "{bcrypt}";

    /**
     * {noop} 加密的特征码
     */
    String NOOP = "{noop}";

}
