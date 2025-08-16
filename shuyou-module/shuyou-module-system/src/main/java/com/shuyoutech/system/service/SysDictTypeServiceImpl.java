package com.shuyoutech.system.service;

import com.shuyoutech.common.cache.util.CacheUtils;
import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.model.ParamUnique;
import com.shuyoutech.common.core.util.CollectionUtils;
import com.shuyoutech.common.core.util.MapstructUtils;
import com.shuyoutech.common.core.util.StringUtils;
import com.shuyoutech.common.mongodb.MongoUtils;
import com.shuyoutech.common.redis.constant.CacheConstants;
import com.shuyoutech.common.web.service.SuperServiceImpl;
import com.shuyoutech.system.domain.bo.SysDictTypeBo;
import com.shuyoutech.system.domain.entity.SysDictDataEntity;
import com.shuyoutech.system.domain.entity.SysDictTypeEntity;
import com.shuyoutech.system.domain.vo.SysDictTypeVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author YangChao
 * @date 2025-07-07 09:55:16
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class SysDictTypeServiceImpl extends SuperServiceImpl<SysDictTypeEntity, SysDictTypeVo> implements SysDictTypeService {

    @Override
    public List<SysDictTypeVo> convertTo(List<SysDictTypeEntity> list) {
        return MapstructUtils.convert(list, this.voClass);
    }

    public SysDictTypeVo convertTo(SysDictTypeEntity entity) {
        return convertTo(Collections.singletonList(entity)).getFirst();
    }

    @Override
    public Query buildQuery(SysDictTypeBo bo) {
        Query query = new Query();
        if (StringUtils.isNotBlank(bo.getDictCode())) {
            query.addCriteria(Criteria.where("dictCode").regex(Pattern.compile(String.format("^.*%s.*$", bo.getDictCode()), Pattern.CASE_INSENSITIVE)));
        }
        if (StringUtils.isNotBlank(bo.getDictName())) {
            query.addCriteria(Criteria.where("dictName").regex(Pattern.compile(String.format("^.*%s.*$", bo.getDictName()), Pattern.CASE_INSENSITIVE)));
        }
        return query;
    }

    @Override
    public boolean checkUnique(ParamUnique paramUnique) {
        Query query = new Query();
        query.addCriteria(Criteria.where(paramUnique.getParamCode()).is(paramUnique.getParamValue()));
        SysDictTypeEntity role = this.selectOne(query);
        if (null == role) {
            return true;
        }
        return StringUtils.isNotBlank(paramUnique.getId()) && paramUnique.getId().equals(role.getId());
    }

    @Override
    public PageResult<SysDictTypeVo> page(PageQuery<SysDictTypeBo> pageQuery) {
        PageQuery<Query> page = pageQuery.buildPage();
        page.setQuery(buildQuery(pageQuery.getQuery()));
        return this.selectPageVo(page);
    }

    @Override
    public SysDictTypeVo detail(String id) {
        SysDictTypeEntity entity = this.getById(id);
        return convertTo(entity);
    }

    @Override
    public String saveSysDictType(SysDictTypeBo bo) {
        SysDictTypeEntity entity = this.save(bo);
        return null == entity ? null : entity.getId();
    }

    @Override
    public boolean updateSysDictType(SysDictTypeBo bo) {
        return this.patch(bo);
    }

    @Override
    public boolean deleteSysDictType(List<String> ids) {
        List<SysDictTypeEntity> dictList = this.getByIds(ids);
        if (CollectionUtils.isEmpty(dictList)) {
            return false;
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("dictTypeId").in(ids));
        MongoUtils.delete(query, SysDictDataEntity.class);
        for (SysDictTypeEntity dict : dictList) {
            CacheUtils.evict(CacheConstants.CACHE_DICT_KEY, dict.getDictCode());
        }
        return this.deleteByIds(ids);
    }

}