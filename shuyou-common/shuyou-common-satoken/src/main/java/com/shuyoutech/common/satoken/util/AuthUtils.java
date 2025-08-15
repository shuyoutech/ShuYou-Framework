package com.shuyoutech.common.satoken.util;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.shuyoutech.api.model.LoginUser;
import com.shuyoutech.common.core.constant.AuthConstants;
import com.shuyoutech.common.core.constant.CommonConstants;
import com.shuyoutech.common.core.exception.BusinessException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;

/**
 * @author YangChao
 * @date 2025-05-16 15:54
 **/
@Slf4j
public class AuthUtils {

    private static SecretKey getSecretKey() {
        byte[] encodeKey = Decoders.BASE64.decode(CommonConstants.JWT_SECRET);
        return Keys.hmacShaKeyFor(encodeKey);
    }

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
        if (null == userId) {
            return null;
        }
        return userId.toString();
    }

    /**
     * 获取当前登录用户名称
     */
    public static String getLoginUserName() {
        Object userName = StpUtil.getExtra(CommonConstants.USER_NAME);
        if (null == userName) {
            return null;
        }
        return userName.toString();
    }

    /**
     * 获取当前登录用户类型
     */
    public static String getLoginUserType() {
        Object userType = StpUtil.getExtra(CommonConstants.USER_TYPE);
        if (null == userType) {
            return null;
        }
        return userType.toString();
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
