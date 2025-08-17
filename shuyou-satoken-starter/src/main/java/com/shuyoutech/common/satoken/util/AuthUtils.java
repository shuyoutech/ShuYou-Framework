package com.shuyoutech.common.satoken.util;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.shuyoutech.common.core.constant.CommonConstants;
import com.shuyoutech.common.core.exception.BusinessException;
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
     * 获取当前登录用户信息
     */
    public static LoginUser getLoginUser() {
        return getLoginUser(true);
    }

    /**
     * 获取当前登录用户信息
     */
    public static LoginUser getLoginUser(boolean error) {
        try {
            return JSONObject.parseObject(JSONObject.toJSONString(StpUtil.getTokenSession().get(AuthConstants.LOGIN_USER)), LoginUser.class);
        } catch (Exception e) {
            log.error("getLoginUser error:{}", e.getMessage());
            if (error) {
                throw new BusinessException(403, "登录已失效，请重新登陆");
            } else {
                return null;
            }
        }
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
     * 获取当前登录用户组织ID
     */
    public static String getLoginOrgId() {
        LoginUser loginUser = getLoginUser(false);
        if (null == loginUser) {
            return null;
        }
        return loginUser.getOrgId();
    }

    /**
     * 获取用户基于token
     */
    public static LoginUser getLoginUser(String token) {
        SaSession session = StpUtil.getTokenSessionByToken(token);
        if (ObjectUtil.isNull(session)) {
            return null;
        }
        return session.getModel(AuthConstants.LOGIN_USER, LoginUser.class);
    }

}
