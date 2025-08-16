package com.shuyoutech.system.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.shuyoutech.api.model.LoginUser;
import com.shuyoutech.common.core.util.MapstructUtils;
import com.shuyoutech.common.core.util.StringUtils;
import com.shuyoutech.common.mongodb.MongoUtils;
import com.shuyoutech.common.redis.constant.CacheConstants;
import com.shuyoutech.system.domain.entity.*;
import com.shuyoutech.system.domain.vo.SysDictDataVo;
import jodd.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.shuyoutech.common.redis.constant.CacheConstants.*;

/**
 * @author YangChao
 * @date 2025-01-15 14:42
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class CacheServiceImpl implements CacheService {

    @Override
    @Cacheable(cacheNames = CacheConstants.CACHE_DICT_KEY, key = "#dictCode")
    public List<SysDictDataVo> queryDictList(String dictCode) {
        if (StringUtil.isEmpty(dictCode)) {
            return null;
        }
        Query query1 = new Query();
        query1.addCriteria(Criteria.where("dictCode").is(dictCode));
        SysDictTypeEntity dictType = MongoUtils.selectOne(query1, SysDictTypeEntity.class);
        if (null == dictType) {
            return null;
        }
        Query query2 = new Query();
        query2.addCriteria(Criteria.where("dictTypeId").is(dictType.getId()));
        query2.with(Sort.by(Sort.Direction.ASC, "dictSort"));
        List<SysDictDataEntity> dataList = MongoUtils.selectList(query2, SysDictDataEntity.class);
        if (CollectionUtil.isEmpty(dataList)) {
            return null;
        }
        return MapstructUtils.convert(dataList, SysDictDataVo.class);
    }

    @Override
    @Cacheable(cacheNames = "TOKEN_KEY", key = "#token")
    public LoginUser checkToken(String token) {
        String userId = StringUtils.toStringOrEmpty(StpUtil.getLoginIdByToken(token));
        if (StringUtils.isBlank(userId)) {
            log.error("checkToken ================ token:{} is not exist", token);
            return null;
        }
        SysUserEntity sysUser = MongoUtils.getById(userId, SysUserEntity.class);
        if (null == sysUser) {
            log.error("checkToken ================ userId:{} is not exist", userId);
            return null;
        }
        return MapstructUtils.convert(sysUser, LoginUser.class);
    }

    @Override
    @Cacheable(cacheNames = "USER_ROLE_KEY", key = "#userId")
    public Set<String> selectRolePermissionByUserId(String userId) {
        Set<String> permsSet = new HashSet<>();
        SysUserEntity user = MongoUtils.getById(userId, SysUserEntity.class);
        if (null == user) {
            return permsSet;
        }
        Set<String> roleIds = user.getRoleIds();
        List<SysRoleEntity> roles = MongoUtils.getByIds(roleIds, SysRoleEntity.class);
        if (CollectionUtil.isEmpty(roles)) {
            return permsSet;
        }
        roles.forEach(role -> permsSet.add(role.getRoleCode()));
        return permsSet;
    }

    @Override
    @Cacheable(cacheNames = "USER_MENU_KEY", key = "#userId")
    public Set<String> selectMenuPermsByUserId(String userId) {
        Set<String> permsSet = new HashSet<>();
        SysUserEntity user = MongoUtils.getById(userId, SysUserEntity.class);
        if (null == user) {
            return permsSet;
        }
        Set<String> roleIds = user.getRoleIds();
        List<SysRoleEntity> roles = MongoUtils.getByIds(roleIds, SysRoleEntity.class);
        if (CollectionUtil.isEmpty(roles)) {
            return permsSet;
        }
        Set<String> menuIds = new HashSet<>();
        roles.forEach(role -> menuIds.addAll(role.getMenuIds()));
        List<SysMenuEntity> menus = MongoUtils.getByIds(menuIds, SysMenuEntity.class);
        if (CollectionUtil.isEmpty(menus)) {
            return permsSet;
        }
        menus.forEach(menu -> permsSet.add(menu.getPerms()));
        return permsSet;
    }

    @Override
    @Cacheable(cacheNames = CACHE_USER_KEY, key = "#userId")
    public String getUserName(String userId) {
        SysUserEntity user = MongoUtils.getById(userId, SysUserEntity.class);
        return null == user ? null : user.getRealName();
    }

    @Override
    @Cacheable(cacheNames = CACHE_ORG_KEY, key = "#orgId")
    public String getOrgName(String orgId) {
        SysOrgEntity org = MongoUtils.getById(orgId, SysOrgEntity.class);
        return null == org ? null : org.getOrgName();
    }

    @Override
    @Cacheable(cacheNames = CACHE_ROLE_KEY, key = "#roleId")
    public String getRoleName(String roleId) {
        SysRoleEntity role = MongoUtils.getById(roleId, SysRoleEntity.class);
        return null == role ? null : role.getRoleName();
    }

    @Override
    @Cacheable(cacheNames = CACHE_POST_KEY, key = "#postId")
    public String getPostName(String postId) {
        SysPostEntity post = MongoUtils.getById(postId, SysPostEntity.class);
        return null == post ? null : post.getPostName();
    }

}
