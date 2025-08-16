package com.shuyoutech.system.service;

import com.shuyoutech.api.model.LoginUserPost;
import com.shuyoutech.api.model.LoginUserRole;
import com.shuyoutech.common.core.util.CollectionUtils;
import com.shuyoutech.common.core.util.MapUtils;
import com.shuyoutech.common.core.util.StreamUtils;
import com.shuyoutech.common.mongodb.MongoUtils;
import com.shuyoutech.system.domain.entity.SysOrgEntity;
import com.shuyoutech.system.domain.entity.SysPostEntity;
import com.shuyoutech.system.domain.entity.SysRoleEntity;
import com.shuyoutech.system.domain.entity.SysUserEntity;
import com.shuyoutech.system.domain.vo.SysDictDataVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author YangChao
 * @date 2025-07-08 11:56
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class CachePlusServiceImpl implements CachePlusService {

    @Override
    public Map<String, String> translateByDictCode(String dictCode) {
        Map<String, String> result = MapUtils.newHashMap();
        List<SysDictDataVo> dictItemList = cacheService.queryDictList(dictCode);
        if (CollectionUtils.isEmpty(dictItemList)) {
            return result;
        }
        for (SysDictDataVo dictItem : dictItemList) {
            result.put(dictItem.getDictValue(), dictItem.getDictLabel());
        }
        return result;
    }

    @Override
    public Map<String, String> reverseByDictCode(String dictCode) {
        Map<String, String> result = MapUtils.newHashMap();
        List<SysDictDataVo> dictItemList = cacheService.queryDictList(dictCode);
        if (CollectionUtils.isEmpty(dictItemList)) {
            return result;
        }
        for (SysDictDataVo dictItem : dictItemList) {
            result.put(dictItem.getDictLabel(), dictItem.getDictValue());
        }
        return result;
    }

    @Override
    public Set<String> getRolePermission(String userId) {
        return cacheService.selectRolePermissionByUserId(userId);
    }

    @Override
    public Set<String> getMenuPermission(String userId) {
        return cacheService.selectMenuPermsByUserId(userId);
    }

    @Override
    public List<LoginUserRole> selectRolesByUserId(String userId) {
        List<LoginUserRole> list = CollectionUtils.newArrayList();
        SysUserEntity user = MongoUtils.getById(userId, SysUserEntity.class);
        if (null == user) {
            return list;
        }
        Set<String> roleIds = user.getRoleIds();
        if (CollectionUtils.isEmpty(roleIds)) {
            return list;
        }
        List<SysRoleEntity> roles = MongoUtils.getByIds(roleIds, SysRoleEntity.class);
        if (CollectionUtils.isEmpty(roles)) {
            return list;
        }
        roles.forEach(role -> list.add(LoginUserRole.builder() //
                .roleId(role.getId()) //
                .roleName(role.getRoleName()) //
                .dataScope(role.getDataScope()) //
                .build()));
        return list;
    }

    @Override
    public List<LoginUserPost> selectPostsByUserId(String userId) {
        List<LoginUserPost> list = CollectionUtils.newArrayList();
        SysUserEntity user = MongoUtils.getById(userId, SysUserEntity.class);
        if (null == user) {
            return list;
        }
        Set<String> postIds = user.getPostIds();
        if (CollectionUtils.isEmpty(postIds)) {
            return list;
        }
        List<SysPostEntity> posts = MongoUtils.getByIds(postIds, SysPostEntity.class);
        if (CollectionUtils.isEmpty(posts)) {
            return list;
        }
        posts.forEach(post -> list.add(LoginUserPost.builder() //
                .postId(post.getId()) //
                .postCode(post.getPostCode()) //
                .postName(post.getPostName()) //
                .build()));
        return list;
    }

    @Override
    public Map<String, String> translateUserName(Set<String> userIds) {
        Map<String, String> result = MapUtils.newHashMap();
        if (CollectionUtils.isEmpty(userIds)) {
            return result;
        }
        if (userIds.size() > 1) {
            List<SysUserEntity> userList = MongoUtils.getByIds(userIds, SysUserEntity.class);
            return StreamUtils.toMap(userList, SysUserEntity::getId, SysUserEntity::getRealName);
        }
        String userId = CollectionUtils.get(userIds, 0);
        String userName = cacheService.getUserName(userId);
        result.put(userId, userName);
        return result;
    }

    @Override
    public Map<String, String> translateOrgName(Set<String> orgIds) {
        Map<String, String> result = MapUtils.newHashMap();
        if (CollectionUtils.isEmpty(orgIds)) {
            return result;
        }
        if (orgIds.size() > 1) {
            List<SysOrgEntity> orgList = MongoUtils.getByIds(orgIds, SysOrgEntity.class);
            return StreamUtils.toMap(orgList, SysOrgEntity::getId, SysOrgEntity::getOrgName);
        }
        String orgId = CollectionUtils.get(orgIds, 0);
        String orgName = cacheService.getOrgName(orgId);
        result.put(orgId, orgName);
        return result;
    }

    @Override
    public Map<String, String> translateRoleName(Set<String> roleIds) {
        Map<String, String> result = MapUtils.newHashMap();
        if (CollectionUtils.isEmpty(roleIds)) {
            return result;
        }
        if (roleIds.size() > 1) {
            List<SysRoleEntity> roleList = MongoUtils.getByIds(roleIds, SysRoleEntity.class);
            return StreamUtils.toMap(roleList, SysRoleEntity::getId, SysRoleEntity::getRoleName);
        }
        String roleId = CollectionUtils.get(roleIds, 0);
        String roleName = cacheService.getRoleName(roleId);
        result.put(roleId, roleName);
        return result;
    }

    @Override
    public Map<String, String> translatePostName(Set<String> postIds) {
        Map<String, String> result = MapUtils.newHashMap();
        if (CollectionUtils.isEmpty(postIds)) {
            return result;
        }
        if (postIds.size() > 1) {
            List<SysPostEntity> postList = MongoUtils.getByIds(postIds, SysPostEntity.class);
            return StreamUtils.toMap(postList, SysPostEntity::getId, SysPostEntity::getPostName);
        }
        String postId = CollectionUtils.get(postIds, 0);
        String postName = cacheService.getPostName(postId);
        result.put(postId, postName);
        return result;
    }

    private final CacheService cacheService;

}
