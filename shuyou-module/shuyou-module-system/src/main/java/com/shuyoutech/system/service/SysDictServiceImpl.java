package com.shuyoutech.system.service;

import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.model.ParamUnique;
import com.shuyoutech.common.core.util.MapstructUtils;
import com.shuyoutech.common.core.util.StringUtils;
import com.shuyoutech.common.web.service.SuperServiceImpl;
import com.shuyoutech.system.domain.bo.SysDictBo;
import com.shuyoutech.system.domain.entity.SysDictEntity;
import com.shuyoutech.system.domain.vo.SysDictVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author YangChao
 * @date 2025-07-07 10:17:54
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class SysDictServiceImpl extends SuperServiceImpl<SysDictEntity, SysDictVo> implements SysDictService {

    @Override
    public List<SysDictVo> convertTo(List<SysDictEntity> list) {
        return MapstructUtils.convert(list, this.voClass);
    }

    public SysDictVo convertTo(SysDictEntity entity) {
        return convertTo(Collections.singletonList(entity)).getFirst();
    }

    @Override
    public Query buildQuery(SysDictBo bo) {
        Query query = new Query();
        return query;
    }

    @Override
    public boolean checkUnique(ParamUnique paramUnique) {
        Query query = new Query();
        query.addCriteria(Criteria.where(paramUnique.getParamCode()).is(paramUnique.getParamValue()));
        SysDictEntity role = this.selectOne(query);
        if (null == role) {
            return true;
        }
        return StringUtils.isNotBlank(paramUnique.getId()) && paramUnique.getId().equals(role.getId());
    }

    @Override
    public PageResult<SysDictVo> page(PageQuery<SysDictBo> pageQuery) {
        PageQuery<Query> page = pageQuery.buildPage();
        page.setQuery(buildQuery(pageQuery.getQuery()));
        return this.selectPageVo(page);
    }

    @Override
    public SysDictVo detail(String id) {
        SysDictEntity entity = this.getById(id);
        return convertTo(entity);
    }

    @Override
    public String saveSysDict(SysDictBo bo) {
        SysDictEntity entity = this.save(bo);
        return null == entity ? null : entity.getId();
    }

    @Override
    public boolean updateSysDict(SysDictBo bo) {
        return this.patch(bo);
    }

    @Override
    public boolean deleteSysDict(List<String> ids) {
        return this.deleteByIds(ids);
    }

}