package com.shuyoutech.system.service;

import com.shuyoutech.common.cache.util.CacheUtils;
import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.model.ParamUnique;
import com.shuyoutech.common.core.util.CollectionUtils;
import com.shuyoutech.common.core.util.MapstructUtils;
import com.shuyoutech.common.core.util.StringUtils;
import com.shuyoutech.common.redis.constant.CacheConstants;
import com.shuyoutech.common.web.service.SuperServiceImpl;
import com.shuyoutech.system.domain.bo.SysDictDataBo;
import com.shuyoutech.system.domain.entity.SysDictDataEntity;
import com.shuyoutech.system.domain.entity.SysDictTypeEntity;
import com.shuyoutech.system.domain.vo.SysDictDataVo;
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
 * @date 2025-07-07 10:02:47
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class SysDictDataServiceImpl extends SuperServiceImpl<SysDictDataEntity, SysDictDataVo> implements SysDictDataService {

    @Override
    public List<SysDictDataVo> convertTo(List<SysDictDataEntity> list) {
        return MapstructUtils.convert(list, this.voClass);
    }

    public SysDictDataVo convertTo(SysDictDataEntity entity) {
        return convertTo(Collections.singletonList(entity)).getFirst();
    }

    @Override
    public Query buildQuery(SysDictDataBo bo) {
        Query query = new Query();
        if (StringUtils.isNotBlank(bo.getDictTypeId())) {
            query.addCriteria(Criteria.where("dictTypeId").is(bo.getDictTypeId()));
        }
        if (StringUtils.isNotBlank(bo.getDictLabel())) {
            query.addCriteria(Criteria.where("dictLabel").regex(Pattern.compile(String.format("^.*%s.*$", bo.getDictLabel()), Pattern.CASE_INSENSITIVE)));
        }
        return query;
    }

    @Override
    public boolean checkUnique(ParamUnique paramUnique) {
        Query query = new Query();
        query.addCriteria(Criteria.where(paramUnique.getParamCode()).is(paramUnique.getParamValue()));
        SysDictDataEntity role = this.selectOne(query);
        if (null == role) {
            return true;
        }
        return StringUtils.isNotBlank(paramUnique.getId()) && paramUnique.getId().equals(role.getId());
    }

    @Override
    public PageResult<SysDictDataVo> page(PageQuery<SysDictDataBo> pageQuery) {
        PageQuery<Query> page = pageQuery.buildPage();
        page.setQuery(buildQuery(pageQuery.getQuery()));
        return this.selectPageVo(page);
    }

    @Override
    public SysDictDataVo detail(String id) {
        SysDictDataEntity entity = this.getById(id);
        return convertTo(entity);
    }

    @Override
    public String saveSysDictData(SysDictDataBo bo) {
        SysDictDataEntity entity = this.save(bo);
        return null == entity ? null : entity.getId();
    }

    @Override
    public boolean updateSysDictData(SysDictDataBo bo) {
        return this.patch(bo);
    }

    @Override
    public boolean deleteSysDictData(List<String> ids) {
        List<SysDictDataEntity> dataList = this.getByIds(ids);
        if (CollectionUtils.isEmpty(dataList)) {
            return false;
        }
        String dictTypeId = dataList.getFirst().getDictTypeId();
        SysDictTypeEntity dictType = sysDictTypeService.getById(dictTypeId);
        if (null == dictType) {
            return false;
        }
        boolean result = this.deleteByIds(ids);
        CacheUtils.evict(CacheConstants.CACHE_DICT_KEY, dictType.getDictCode());
        return result;
    }

    private final SysDictTypeService sysDictTypeService;

}