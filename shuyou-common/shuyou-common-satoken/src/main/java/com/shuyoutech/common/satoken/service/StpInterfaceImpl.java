package com.shuyoutech.common.satoken.service;

import cn.dev33.satoken.stp.StpInterface;
import com.shuyoutech.api.model.LoginUser;
import com.shuyoutech.api.service.RemoteSystemService;
import com.shuyoutech.common.core.util.SpringUtils;
import com.shuyoutech.common.satoken.util.AuthUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author YangChao
 * @date 2025-07-08 15:36
 **/
public class StpInterfaceImpl implements StpInterface {

    /**
     * 返回一个账号所拥有的菜单权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        LoginUser loginUser = AuthUtils.getLoginUser();
        if (null == loginUser || !loginUser.getId().equals(loginId)) {
            RemoteSystemService remoteSystemService = SpringUtils.getBean(RemoteSystemService.class);
            return new ArrayList<>(remoteSystemService.getMenuPermission(loginId.toString()));
        }
        return new ArrayList<>(loginUser.getMenuPermission());
    }

    /**
     * 返回一个账号所拥有的角色标识集合
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        LoginUser loginUser = AuthUtils.getLoginUser();
        if (null == loginUser || !loginUser.getId().equals(loginId)) {
            RemoteSystemService remoteSystemService = SpringUtils.getBean(RemoteSystemService.class);
            return new ArrayList<>(remoteSystemService.getRolePermission(loginId.toString()));
        }
        return new ArrayList<>(loginUser.getRolePermission());
    }

}
