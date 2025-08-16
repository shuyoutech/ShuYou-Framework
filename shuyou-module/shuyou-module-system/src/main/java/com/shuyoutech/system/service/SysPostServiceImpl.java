package com.shuyoutech.system.service;

import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.model.ParamUnique;
import com.shuyoutech.common.core.util.MapstructUtils;
import com.shuyoutech.common.core.util.StringUtils;
import com.shuyoutech.common.mongodb.MongoUtils;
import com.shuyoutech.common.web.service.SuperServiceImpl;
import com.shuyoutech.system.domain.bo.SysPostBo;
import com.shuyoutech.system.domain.entity.SysPostEntity;
import com.shuyoutech.system.domain.vo.SysPostVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author YangChao
 * @date 2025-07-07 10:33:09
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class SysPostServiceImpl extends SuperServiceImpl<SysPostEntity, SysPostVo> implements SysPostService {

    @Override
    public List<SysPostVo> convertTo(List<SysPostEntity> list) {
        return MapstructUtils.convert(list, this.voClass);
    }

    public SysPostVo convertTo(SysPostEntity entity) {
        return convertTo(Collections.singletonList(entity)).getFirst();
    }

    @Override
    public Query buildQuery(SysPostBo bo) {
        Query query = new Query();
        return query;
    }

    @Override
    public boolean checkUnique(ParamUnique paramUnique) {
        Query query = new Query();
        query.addCriteria(Criteria.where(paramUnique.getParamCode()).is(paramUnique.getParamValue()));
        SysPostEntity role = this.selectOne(query);
        if (null == role) {
            return true;
        }
        return StringUtils.isNotBlank(paramUnique.getId()) && paramUnique.getId().equals(role.getId());
    }

    @Override
    public PageResult<SysPostVo> page(PageQuery<SysPostBo> pageQuery) {
        PageQuery<Query> page = pageQuery.buildPage();
        page.setQuery(buildQuery(pageQuery.getQuery()));
        return this.selectPageVo(page);
    }

    @Override
    public SysPostVo detail(String id) {
        SysPostEntity entity = this.getById(id);
        return convertTo(entity);
    }

    @Override
    public String saveSysPost(SysPostBo bo) {
        SysPostEntity entity = this.save(bo);
        return null == entity ? null : entity.getId();
    }

    @Override
    public boolean updateSysPost(SysPostBo bo) {
        return this.patch(bo);
    }

    @Override
    public boolean deleteSysPost(List<String> ids) {
        return this.deleteByIds(ids);
    }

    @Override
    public boolean statusSysPost(String id, String status) {
        SysPostEntity entity = this.getById(id);
        if (null == entity) {
            return false;
        }
        Update update = new Update();
        update.set("status", status);
        return MongoUtils.patch(id, update, SysPostEntity.class);
    }

}