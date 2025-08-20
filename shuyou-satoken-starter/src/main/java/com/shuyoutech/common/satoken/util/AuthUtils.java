package com.shuyoutech.common.satoken.util;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson2.JSONObject;
import com.shuyoutech.common.core.constant.CommonConstants;
import com.shuyoutech.common.core.util.StringUtils;
import com.shuyoutech.common.satoken.constant.AuthConstants;
import com.shuyoutech.common.satoken.model.LoginUser;
import lombok.extern.slf4j.Slf4j;

/**
 * @author YangChao
 * @date 2025-05-16 15:54
 **/
@Slf4j
public class AuthUtils {

    /**
     * 获取当前admin端管理登录用户信息
     */
    public static LoginUser getLoginUser() {
        return JSONObject.parseObject(JSONObject.toJSONString(StpUtil.getTokenSession().get(AuthConstants.LOGIN_USER)), LoginUser.class);
    }

    /**
     * 获取当前app端登录用户信息
     */
    public static JSONObject getAppLoginUser() {
        return JSONObject.parseObject(JSONObject.toJSONString(StpUtil.getTokenSession().get(AuthConstants.LOGIN_USER)));
    }

    /**
     * 获取当前登录用户ID
     */
    public static String getLoginUserId() {
        Object userId = StpUtil.getExtra(CommonConstants.USER_ID);
        return StringUtils.toStringOrEmpty(userId);
    }

    /**
     * 获取当前登录用户名称
     */
    public static String getLoginUserName() {
        Object userName = StpUtil.getExtra(CommonConstants.USER_NAME);
        return StringUtils.toStringOrEmpty(userName);
    }

    /**
     * 获取当前登录用户类型
     */
    public static String getLoginUserType() {
        Object userType = StpUtil.getExtra(CommonConstants.USER_TYPE);
        return StringUtils.toStringOrEmpty(userType);
    }

    /**
     * 获取当前登录用户第三方唯一标识
     */
    public static String getLoginUserOpenid() {
        Object openid = StpUtil.getExtra(CommonConstants.USER_OPENID);
        return StringUtils.toStringOrEmpty(openid);
    }

    /**
     * 获取当前登录用户组织ID
     */
    public static String getLoginOrgId() {
        return getLoginUser().getOrgId();
    }

}
