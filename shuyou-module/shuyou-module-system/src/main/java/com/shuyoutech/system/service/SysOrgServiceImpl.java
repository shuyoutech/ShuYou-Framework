package com.shuyoutech.system.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.model.ParamUnique;
import com.shuyoutech.common.core.model.TreeOption;
import com.shuyoutech.common.core.util.*;
import com.shuyoutech.common.mongodb.MongoUtils;
import com.shuyoutech.common.web.service.SuperTreeServiceImpl;
import com.shuyoutech.system.domain.bo.SysOrgBo;
import com.shuyoutech.system.domain.entity.SysOrgEntity;
import com.shuyoutech.system.domain.vo.SysOrgVo;
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
 * @date 2025-07-07 09:17:49
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class SysOrgServiceImpl extends SuperTreeServiceImpl<SysOrgEntity, SysOrgVo> implements SysOrgService {

    @Override
    public List<SysOrgVo> convertTo(List<SysOrgEntity> list) {
        List<SysOrgVo> result = CollectionUtils.newArrayList();
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        Map<String, String> statusMap = cachePlusService.translateByDictCode(DictTypeEnum.STATUS_TYPE.getValue());
        Map<String, String> orgMap = cachePlusService.translateByDictCode(DictTypeEnum.ORG_TYPE.getValue());
        Set<String> userIds = StreamUtils.toSet(list, SysOrgEntity::getDirectorId);
        Map<String, String> userMap = cachePlusService.translateUserName(userIds);
        list.forEach(e -> {
            SysOrgVo vo = MapstructUtils.convert(e, this.voClass);
            if (null != vo) {
                vo.setStatusName(MapUtils.getStr(statusMap, e.getStatus(), ""));
                vo.setOrgTypeName(MapUtils.getStr(orgMap, e.getOrgType(), ""));
                vo.setDirectorName(MapUtils.getStr(userMap, e.getDirectorId(), ""));
                result.add(vo);
            }
        });
        return result;
    }

    public SysOrgVo convertTo(SysOrgEntity entity) {
        return convertTo(Collections.singletonList(entity)).getFirst();
    }

    @Override
    public Query buildQuery(SysOrgBo bo) {
        Query query = new Query();
        if (StringUtils.isNotBlank(bo.getStatus())) {
            query.addCriteria(Criteria.where("status").is(bo.getStatus()));
        }
        if (StringUtils.isNotBlank(bo.getParentId())) {
            query.addCriteria(Criteria.where("parentId").is(bo.getParentId()));
        }
        if (StringUtils.isNotBlank(bo.getOrgType())) {
            query.addCriteria(Criteria.where("orgType").is(bo.getOrgType()));
        }
        if (StringUtils.isNotBlank(bo.getOrgName())) {
            query.addCriteria(Criteria.where("orgName").regex(Pattern.compile(String.format("^.*%s.*$", bo.getOrgName()), Pattern.CASE_INSENSITIVE)));
        }
        return query;
    }

    @Override
    public boolean checkUnique(ParamUnique paramUnique) {
        Query query = new Query();
        query.addCriteria(Criteria.where(paramUnique.getParamCode()).is(paramUnique.getParamValue()));
        SysOrgEntity role = this.selectOne(query);
        if (null == role) {
            return true;
        }
        return StringUtils.isNotBlank(paramUnique.getId()) && paramUnique.getId().equals(role.getId());
    }

    @Override
    public PageResult<SysOrgVo> page(PageQuery<SysOrgBo> pageQuery) {
        PageQuery<Query> page = pageQuery.buildPage();
        page.setQuery(buildQuery(pageQuery.getQuery()));
        return this.selectPageVo(page);
    }

    @Override
    public SysOrgVo detail(String id) {
        SysOrgEntity entity = this.getById(id);
        return convertTo(entity);
    }

    @Override
    public String saveSysOrg(SysOrgBo bo) {
        SysOrgEntity entity = this.save(bo);
        return null == entity ? null : entity.getId();
    }

    @Override
    public boolean updateSysOrg(SysOrgBo bo) {
        return this.patch(bo);
    }

    @Override
    public boolean deleteSysOrg(List<String> ids) {
        return this.deleteByIds(ids);
    }

    @Override
    public boolean statusSysOrg(String id, String status) {
        SysOrgEntity entity = this.getById(id);
        if (null == entity) {
            return false;
        }
        Update update = new Update();
        update.set("status", status);
        return MongoUtils.patch(id, update, SysOrgEntity.class);
    }

    @Override
    public List<Tree<String>> tree(SysOrgBo bo) {
        Query query = this.buildQuery(bo);
        List<SysOrgVo> orgList = this.selectListVo(query);
        if (CollectionUtils.isEmpty(orgList)) {
            return CollectionUtils.newArrayList();
        }
        List<TreeOption> list = CollectionUtil.newArrayList();
        for (SysOrgVo org : orgList) {
            list.add(TreeOption.builder() //
                    .parentId(org.getParentId()) //
                    .label(org.getOrgName()) //
                    .type(org.getOrgType()) //
                    .value(org.getId()) //
                    .sort(org.getOrgSort()) //
                    .extra(BeanUtils.beanToMap(org)) //
                    .build());
        }
        TreeNodeConfig treeNodeConfig = new TreeNodeConfig();
        treeNodeConfig.setWeightKey("sort");
        treeNodeConfig.setIdKey("value");
        treeNodeConfig.setNameKey("label");
        return TreeUtils.build(list, "0", treeNodeConfig, (treeOption, tree) -> {
            tree.setId(treeOption.getValue()).setParentId(treeOption.getParentId())//
                    .setName(treeOption.getLabel())//
                    .setWeight(treeOption.getSort())//
                    .putExtra("org", treeOption.getExtra());
        });
    }

    private final CachePlusService cachePlusService;

}