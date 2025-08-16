package com.shuyoutech.system.service;

import com.shuyoutech.api.model.LoginUser;
import com.shuyoutech.system.domain.vo.SysDictDataVo;

import java.util.List;
import java.util.Set;

/**
 * @author YangChao
 * @date 2025-01-15 14:42
 **/
public interface CacheService {

    /**
     * 根据字典编码查询字典数据集合
     *
     * @param dictCode 字典编码
     * @return 数据字典项集合
     */
    List<SysDictDataVo> queryDictList(String dictCode);

    /**
     * 根据token查询用户信息
     *
     * @param token 令牌
     * @return 客户信息
     */
    LoginUser checkToken(String token);

    /**
     * 根据用户ID查询角色权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    Set<String> selectRolePermissionByUserId(String userId);

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    Set<String> selectMenuPermsByUserId(String userId);

    /**
     * 根据用户ID查询用户名称
     *
     * @param userId 用户ID
     * @return 用户名称
     */
    String getUserName(String userId);

    /**
     * 根据机构ID查询机构名称
     *
     * @param orgId 机构ID
     * @return 机构名称
     */
    String getOrgName(String orgId);

    /**
     * 根据角色ID查询角色名称
     *
     * @param roleId 角色ID
     * @return 机构名称
     */
    String getRoleName(String roleId);

    /**
     * 根据岗位ID查询岗位名称
     *
     * @param postId 岗位ID
     * @return 岗位名称
     */
    String getPostName(String postId);

}
