package com.shuyoutech.system.service;

import cn.hutool.core.collection.CollectionUtil;
import com.shuyoutech.api.model.LoginUser;
import com.shuyoutech.api.model.RemoteSysFile;
import com.shuyoutech.api.service.RemoteSystemService;
import com.shuyoutech.common.core.util.MapUtils;
import com.shuyoutech.common.core.util.MapstructUtils;
import com.shuyoutech.common.core.util.StreamUtils;
import com.shuyoutech.system.domain.entity.SysFileEntity;
import com.shuyoutech.system.domain.entity.SysUserEntity;
import com.shuyoutech.system.domain.vo.SysDictDataVo;
import com.shuyoutech.system.domain.vo.SysFileVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author YangChao
 * @date 2025-07-10 14:17
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class RemoteSystemServiceImpl implements RemoteSystemService {

    @Override
    public LoginUser getUserByUsername(String username) {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username));
        SysUserEntity user = sysUserService.selectOne(query);
        LoginUser loginUser = MapstructUtils.convert(user, LoginUser.class);
        if (null != loginUser) {
            String userId = loginUser.getId();
            loginUser.setMenuPermission(cachePlusService.getMenuPermission(userId));
            loginUser.setRolePermission(cachePlusService.getRolePermission(userId));
            loginUser.setRoles(cachePlusService.selectRolesByUserId(userId));
            loginUser.setPosts(cachePlusService.selectPostsByUserId(userId));
        }
        return loginUser;
    }

    @Override
    public Boolean passwordMatch(String rawPassword, String encodedPassword) {
        return sysUserService.passwordMatch(rawPassword, encodedPassword);
    }

    @Override
    public Map<String, String> getUserName(Set<String> userIds) {
        Map<String, String> result = MapUtils.newHashMap();
        if (CollectionUtil.isEmpty(userIds)) {
            return result;
        }
        List<SysUserEntity> users = sysUserService.getByIds(userIds);
        if (CollectionUtil.isEmpty(users)) {
            return result;
        }
        return StreamUtils.toMap(users, SysUserEntity::getId, SysUserEntity::getRealName);
    }

    @Override
    public Map<String, String> translateByDictCode(String dictCode) {
        Map<String, String> result = MapUtils.newHashMap();
        List<SysDictDataVo> itemList = cacheService.queryDictList(dictCode);
        if (CollectionUtil.isEmpty(itemList)) {
            return result;
        }
        return StreamUtils.toMap(itemList, SysDictDataVo::getDictValue, SysDictDataVo::getDictLabel);
    }

    @Override
    public Set<String> getMenuPermission(String userId) {
        return cachePlusService.getMenuPermission(userId);
    }

    @Override
    public Set<String> getRolePermission(String userId) {
        return cachePlusService.getRolePermission(userId);
    }

    @Override
    public RemoteSysFile getFileById(String fileId) {
        SysFileEntity sysFile = sysFileService.getById(fileId);
        if (null == sysFile) {
            return null;
        }
        return MapstructUtils.convert(sysFile, RemoteSysFile.class);
    }

    @Override
    public String getFilePath(String fileId) {
        RemoteSysFile file = this.getFileById(fileId);
        if (null == file) {
            return null;
        }
        return file.getFilePath();
    }

    @Override
    public String generatedUrl(String ossId, Long expiration) {
        return sysFileService.generatedUrl(ossId, expiration);
    }

    @Override
    public RemoteSysFile upload(String originalFilename, byte[] data) {
        SysFileVo sysFile = sysFileService.upload(originalFilename, data);
        return MapstructUtils.convert(sysFile, RemoteSysFile.class);
    }

    private final CacheService cacheService;
    private final SysUserService sysUserService;
    private final CachePlusService cachePlusService;
    private final SysFileService sysFileService;

}
