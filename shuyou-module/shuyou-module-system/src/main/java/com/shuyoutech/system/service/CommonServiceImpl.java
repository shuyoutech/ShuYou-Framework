package com.shuyoutech.system.service;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import com.shuyoutech.common.core.exception.BusinessException;
import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.util.CollectionUtils;
import com.shuyoutech.common.core.util.EnumUtils;
import com.shuyoutech.common.core.util.FileUtils;
import com.shuyoutech.common.core.util.MapUtils;
import com.shuyoutech.common.mongodb.MongoUtils;
import com.shuyoutech.system.domain.bo.RoleMenuBo;
import com.shuyoutech.system.domain.bo.RoleUserBo;
import com.shuyoutech.system.domain.bo.RoleUserListBo;
import com.shuyoutech.system.domain.bo.SysUserBo;
import com.shuyoutech.system.domain.entity.SysRoleEntity;
import com.shuyoutech.system.domain.entity.SysUserEntity;
import com.shuyoutech.system.domain.vo.SysUserVo;
import com.shuyoutech.system.enums.FileContentTypeEnum;
import com.shuyoutech.system.enums.TemplateFileEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jodd.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author YangChao
 * @date 2025-07-04 15:33
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class CommonServiceImpl implements CommonService {

    @Override
    public void templateFile(String templateType, HttpServletRequest request, HttpServletResponse response) {
        try {
            String templateFile = EnumUtils.getLabelByValue(TemplateFileEnum.class, templateType);
            if (StringUtil.isBlank(templateFile)) {
                throw new BusinessException("没有对应的模版类型");
            }
            InputStream inputStream = ResourceUtil.getStream("template/" + templateFile);
            String encodeFileName = FileUtils.encodeFileName(request, templateFile);
            FileUtils.setAttachmentResponseHeader(response, encodeFileName);
            response.setContentType(FileContentTypeEnum.XLSX.getLabel());
            response.setHeader("Content-Length", String.valueOf(inputStream.available()));
            IoUtil.copy(inputStream, response.getOutputStream());
        } catch (Exception e) {
            log.error("templateFile ================ exception:{}", e.getMessage());
        }
    }

    @Override
    public PageResult<SysUserVo> grantUserList(PageQuery<SysUserBo> pageQuery) {
        return sysUserService.page(pageQuery);
    }

    @Override
    public PageResult<SysUserVo> unGrantUserList(PageQuery<SysUserBo> pageQuery) {
        return sysUserService.page(pageQuery);
    }

    @Override
    public void cancelGrantUser(RoleUserBo bo) {
        String userId = bo.getUserId();
        String roleId = bo.getRoleId();
        SysUserEntity user = sysUserService.getById(userId);
        if (null == user) {
            return;
        }
        Update update = new Update();
        update.set("roleIds", CollectionUtils.removeAny(user.getRoleIds(), roleId));
        MongoUtils.patch(userId, update, SysUserEntity.class);
    }

    @Override
    public void batchCancelGrantUser(RoleUserListBo bo) {
        List<String> userIds = bo.getUserIds();
        String roleId = bo.getRoleId();
        List<SysUserEntity> users = sysUserService.getByIds(userIds);
        if (CollectionUtils.isEmpty(users)) {
            return;
        }
        Map<String, Update> updateMap = MapUtils.newHashMap();
        Update update;
        for (SysUserEntity user : users) {
            update = new Update();
            update.set("roleIds", CollectionUtils.removeAny(user.getRoleIds(), roleId));
            updateMap.put(user.getId(), update);
        }
        MongoUtils.patchBatch(updateMap, SysUserEntity.class);
    }

    @Override
    public void batchGrantUser(RoleUserListBo bo) {
        List<String> userIds = bo.getUserIds();
        String roleId = bo.getRoleId();
        List<SysUserEntity> users = sysUserService.getByIds(userIds);
        if (CollectionUtils.isEmpty(users)) {
            return;
        }
        Map<String, Update> updateMap = MapUtils.newHashMap();
        Update update;
        Set<String> roleIds;
        for (SysUserEntity user : users) {
            roleIds = CollectionUtils.isEmpty(user.getRoleIds()) ? new HashSet<>() : user.getRoleIds();
            roleIds.add(roleId);
            update = new Update();
            update.set("roleIds", roleIds);
            updateMap.put(user.getId(), update);
        }
        MongoUtils.patchBatch(updateMap, SysUserEntity.class);
    }

    @Override
    public void grantMenu(RoleMenuBo bo) {
        String roleId = bo.getRoleId();
        List<String> menuIds = bo.getMenuIds();
        SysRoleEntity role = sysRoleService.getById(roleId);
        if (null == role) {
            return;
        }
        Update update = new Update();
        update.set("menuIds", menuIds);
        MongoUtils.patch(roleId, update, SysRoleEntity.class);
    }

    private final SysUserService sysUserService;
    private final SysRoleService sysRoleService;

}
