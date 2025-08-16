package com.shuyoutech.system.service;

import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.model.ParamUnique;
import com.shuyoutech.common.core.util.CollectionUtils;
import com.shuyoutech.common.core.util.MapstructUtils;
import com.shuyoutech.common.core.util.StreamUtils;
import com.shuyoutech.common.core.util.StringUtils;
import com.shuyoutech.common.mongodb.MongoUtils;
import com.shuyoutech.common.web.service.SuperServiceImpl;
import com.shuyoutech.system.domain.bo.SysRoleBo;
import com.shuyoutech.system.domain.entity.SysRoleEntity;
import com.shuyoutech.system.domain.entity.SysUserEntity;
import com.shuyoutech.system.domain.vo.SysRoleVo;
import com.shuyoutech.system.enums.DictTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author YangChao
 * @date 2025-07-14 17:32
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends SuperServiceImpl<SysRoleEntity, SysRoleVo> implements SysRoleService {

    @Override
    public List<SysRoleVo> convertTo(List<SysRoleEntity> list) {
        List<SysRoleVo> result = CollectionUtils.newArrayList();
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        Set<String> userIds = CollectionUtils.newHashSet();
        CollectionUtils.addAll(userIds, StreamUtils.toSet(list, SysRoleEntity::getCreateUserId));
        CollectionUtils.addAll(userIds, StreamUtils.toSet(list, SysRoleEntity::getUpdateUserId));
        Map<String, String> userMap = sysUserService.getByIds(userIds, SysUserEntity::getId, SysUserEntity::getRealName);

        Map<String, String> statusMap = cachePlusService.translateByDictCode(DictTypeEnum.STATUS_TYPE.getValue());

        list.forEach(e -> {
            SysRoleVo vo = MapstructUtils.convert(e, this.voClass);
            if (null != vo) {
                vo.setStatusName(statusMap.getOrDefault(e.getStatus(), ""));
                vo.setCreateUserName(userMap.getOrDefault(e.getCreateUserId(), ""));
                vo.setUpdateUserName(userMap.getOrDefault(e.getUpdateUserId(), ""));
                result.add(vo);
            }
        });
        return result;
    }

    public SysRoleVo convertTo(SysRoleEntity entity) {
        return convertTo(Collections.singletonList(entity)).getFirst();
    }

    @Override
    public Query buildQuery(SysRoleBo bo) {
        Query query = new Query();
        if (StringUtils.isNotBlank(bo.getRoleCode())) {
            query.addCriteria(Criteria.where("roleCode").regex(Pattern.compile(String.format("^.*%s.*$", bo.getRoleCode()), Pattern.CASE_INSENSITIVE)));
        }
        if (StringUtils.isNotBlank(bo.getRoleName())) {
            query.addCriteria(Criteria.where("roleName").regex(Pattern.compile(String.format("^.*%s.*$", bo.getRoleName()), Pattern.CASE_INSENSITIVE)));
        }
        return query;
    }

    @Override
    public boolean checkUnique(ParamUnique paramUnique) {
        Query query = new Query();
        query.addCriteria(Criteria.where(paramUnique.getParamCode()).is(paramUnique.getParamValue()));
        SysRoleEntity role = this.selectOne(query);
        if (null == role) {
            return true;
        }
        return StringUtils.isNotBlank(paramUnique.getId()) && paramUnique.getId().equals(role.getId());
    }

    @Override
    public PageResult<SysRoleVo> page(PageQuery<SysRoleBo> pageQuery) {
        PageQuery<Query> page = pageQuery.buildPage();
        page.setQuery(buildQuery(pageQuery.getQuery()));
        return this.selectPageVo(page);
    }

    @Override
    public SysRoleVo detail(String id) {
        SysRoleEntity entity = this.getById(id);
        return convertTo(entity);
    }

    @Override
    public String saveRole(SysRoleBo bo) {
        SysRoleEntity entity = this.save(bo);
        return null == entity ? null : entity.getId();
    }

    @Override
    public boolean updateRole(SysRoleBo bo) {
        return this.patch(bo);
    }

    @Override
    public boolean deleteRole(List<String> ids) {
        return this.deleteByIds(ids);
    }

    @Override
    public boolean statusRole(String id, String status) {
        SysRoleEntity entity = this.getById(id);
        if (null == entity) {
            return false;
        }
        Update update = new Update();
        update.set("status", status);
        return MongoUtils.patch(id, update, SysRoleEntity.class);
    }

    private final CachePlusService cachePlusService;
    private final SysUserService sysUserService;
    private final SysOrgService sysOrgService;

}
