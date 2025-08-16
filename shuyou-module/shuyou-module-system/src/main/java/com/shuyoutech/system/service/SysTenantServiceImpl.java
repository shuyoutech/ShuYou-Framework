package com.shuyoutech.system.service;

import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.model.ParamUnique;
import com.shuyoutech.common.core.util.MapstructUtils;
import com.shuyoutech.common.core.util.StringUtils;
import com.shuyoutech.common.mongodb.MongoUtils;
import com.shuyoutech.common.web.service.SuperServiceImpl;
import com.shuyoutech.system.domain.bo.SysTenantBo;
import com.shuyoutech.system.domain.entity.SysTenantEntity;
import com.shuyoutech.system.domain.vo.SysTenantVo;
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
 * @date 2025-07-07 09:01:15
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class SysTenantServiceImpl extends SuperServiceImpl<SysTenantEntity, SysTenantVo> implements SysTenantService {

    @Override
    public List<SysTenantVo> convertTo(List<SysTenantEntity> list) {
        return MapstructUtils.convert(list, this.voClass);
    }

    public SysTenantVo convertTo(SysTenantEntity entity) {
        return convertTo(Collections.singletonList(entity)).getFirst();
    }

    @Override
    public Query buildQuery(SysTenantBo bo) {
        Query query = new Query();
        return query;
    }

    @Override
    public boolean checkUnique(ParamUnique paramUnique) {
        Query query = new Query();
        query.addCriteria(Criteria.where(paramUnique.getParamCode()).is(paramUnique.getParamValue()));
        SysTenantEntity role = this.selectOne(query);
        if (null == role) {
            return true;
        }
        return StringUtils.isNotBlank(paramUnique.getId()) && paramUnique.getId().equals(role.getId());
    }

    @Override
    public PageResult<SysTenantVo> page(PageQuery<SysTenantBo> pageQuery) {
        PageQuery<Query> page = pageQuery.buildPage();
        page.setQuery(buildQuery(pageQuery.getQuery()));
        return this.selectPageVo(page);
    }

    @Override
    public SysTenantVo detail(String id) {
        SysTenantEntity entity = this.getById(id);
        return convertTo(entity);
    }

    @Override
    public String saveSysTenant(SysTenantBo bo) {
        SysTenantEntity entity = this.save(bo);
        return null == entity ? null : entity.getId();
    }

    @Override
    public boolean updateSysTenant(SysTenantBo bo) {
        return this.patch(bo);
    }

    @Override
    public boolean deleteSysTenant(List<String> ids) {
        return this.deleteByIds(ids);
    }

    @Override
    public boolean statusSysTenant(String id, String status) {
        SysTenantEntity entity = this.getById(id);
        if (null == entity) {
            return false;
        }
        Update update = new Update();
        update.set("status", status);
        return MongoUtils.patch(id, update, SysTenantEntity.class);
    }

}