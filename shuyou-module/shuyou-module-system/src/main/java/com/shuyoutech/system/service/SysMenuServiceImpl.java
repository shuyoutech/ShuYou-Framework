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
import com.shuyoutech.system.domain.bo.SysMenuBo;
import com.shuyoutech.system.domain.entity.SysMenuEntity;
import com.shuyoutech.system.domain.vo.SysMenuVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author YangChao
 * @date 2025-07-07 10:44:24
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl extends SuperTreeServiceImpl<SysMenuEntity, SysMenuVo> implements SysMenuService {

    @Override
    public List<SysMenuVo> convertTo(List<SysMenuEntity> list) {
        return MapstructUtils.convert(list, this.voClass);
    }

    public SysMenuVo convertTo(SysMenuEntity entity) {
        return convertTo(Collections.singletonList(entity)).getFirst();
    }

    @Override
    public Query buildQuery(SysMenuBo bo) {
        Query query = new Query();
        if (StringUtils.isNotBlank(bo.getMenuName())) {
            query.addCriteria(Criteria.where("menuName").regex(Pattern.compile(String.format("^.*%s.*$", bo.getMenuName()), Pattern.CASE_INSENSITIVE)));
        }
        return query;
    }

    @Override
    public boolean checkUnique(ParamUnique paramUnique) {
        Query query = new Query();
        query.addCriteria(Criteria.where(paramUnique.getParamCode()).is(paramUnique.getParamValue()));
        SysMenuEntity role = this.selectOne(query);
        if (null == role) {
            return true;
        }
        return StringUtils.isNotBlank(paramUnique.getId()) && paramUnique.getId().equals(role.getId());
    }

    @Override
    public PageResult<SysMenuVo> page(PageQuery<SysMenuBo> pageQuery) {
        PageQuery<Query> page = pageQuery.buildPage();
        page.setQuery(buildQuery(pageQuery.getQuery()));
        return this.selectPageVo(page);
    }

    @Override
    public SysMenuVo detail(String id) {
        SysMenuEntity entity = this.getById(id);
        return convertTo(entity);
    }

    @Override
    public String saveSysMenu(SysMenuBo bo) {
        SysMenuEntity entity = this.save(bo);
        return null == entity ? null : entity.getId();
    }

    @Override
    public boolean updateSysMenu(SysMenuBo bo) {
        return this.patch(bo);
    }

    @Override
    public boolean deleteSysMenu(List<String> ids) {
        return this.deleteByIds(ids);
    }

    @Override
    public boolean statusSysMenu(String id, String status) {
        SysMenuEntity entity = this.getById(id);
        if (null == entity) {
            return false;
        }
        Update update = new Update();
        update.set("status", status);
        return MongoUtils.patch(id, update, SysMenuEntity.class);
    }

    @Override
    public List<Tree<String>> tree(SysMenuBo bo) {
        Query query = this.buildQuery(bo);
        List<SysMenuEntity> menuList = this.selectList(query);
        if (CollectionUtils.isEmpty(menuList)) {
            return CollectionUtils.newArrayList();
        }
        List<TreeOption> list = CollectionUtil.newArrayList();
        for (SysMenuEntity menu : menuList) {
            list.add(TreeOption.builder() //
                    .parentId(menu.getParentId()) //
                    .label(menu.getMenuName()) //
                    .type(menu.getMenuType()) //
                    .value(menu.getId()) //
                    .sort(menu.getMenuSort()) //
                    .extra(BeanUtils.beanToMap(menu)) //
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
                    .putExtra("menu", treeOption.getExtra());
        });
    }

}