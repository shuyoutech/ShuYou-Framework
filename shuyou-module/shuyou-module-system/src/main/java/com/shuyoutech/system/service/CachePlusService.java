package com.shuyoutech.system.service;

import com.shuyoutech.api.model.LoginUserPost;
import com.shuyoutech.api.model.LoginUserRole;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author YangChao
 * @date 2025-07-08 11:56
 **/
public interface CachePlusService {

    /**
     * 数据字典翻译
     *
     * @param dictCode 字典编码
     * @return 翻译对象
     */
    Map<String, String> translateByDictCode(String dictCode);

    /**
     * 数据字典翻译 Label-Value
     *
     * @param dictCode 字典编码
     * @return 翻译对象
     */
    Map<String, String> reverseByDictCode(String dictCode);

    /**
     * 获取角色数据权限
     *
     * @param userId 用户id
     * @return 角色权限信息
     */
    Set<String> getRolePermission(String userId);

    /**
     * 获取菜单数据权限
     *
     * @param userId 用户id
     * @return 菜单权限信息
     */
    Set<String> getMenuPermission(String userId);

    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<LoginUserRole> selectRolesByUserId(String userId);

    /**
     * 查询用户所属岗位组
     *
     * @param userId 用户ID
     * @return 岗位ID
     */
    List<LoginUserPost> selectPostsByUserId(String userId);

    /**
     * 翻译用户名称
     *
     * @param userIds 用户IDS
     * @return 用户名称
     */
    Map<String, String> translateUserName(Set<String> userIds);

    /**
     * 翻译机构名称
     *
     * @param orgIds 机构IDS
     * @return 机构名称
     */
    Map<String, String> translateOrgName(Set<String> orgIds);

    /**
     * 翻译角色名称
     *
     * @param roleIds 角色IDS
     * @return 角色名称
     */
    Map<String, String> translateRoleName(Set<String> roleIds);

    /**
     * 翻译岗位名称
     *
     * @param postIds 岗位IDS
     * @return 岗位名称
     */
    Map<String, String> translatePostName(Set<String> postIds);


}
