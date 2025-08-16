package com.shuyoutech.aigc.service;

import com.shuyoutech.aigc.domain.bo.AigcProviderBo;
import com.shuyoutech.aigc.domain.entity.AigcProviderEntity;
import com.shuyoutech.aigc.domain.vo.AigcProviderVo;
import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.util.MapstructUtils;
import com.shuyoutech.common.core.util.StreamUtils;
import com.shuyoutech.common.core.util.StringUtils;
import com.shuyoutech.common.web.service.SuperServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author YangChao
 * @date 2025-08-12 22:28:58
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class AigcProviderServiceImpl extends SuperServiceImpl<AigcProviderEntity, AigcProviderVo> implements AigcProviderService {

    @Override
    public List<AigcProviderVo> convertTo(List<AigcProviderEntity> list) {
        return MapstructUtils.convert(list, this.voClass);
    }

    public AigcProviderVo convertTo(AigcProviderEntity entity) {
        return convertTo(Collections.singletonList(entity)).getFirst();
    }

    @Override
    public Query buildQuery(AigcProviderBo bo) {
        Query query = new Query();
        if (StringUtils.isNotBlank(bo.getProviderCode())) {
            query.addCriteria(Criteria.where("providerCode").regex(Pattern.compile(String.format("^.*%s.*$", bo.getProviderCode()), Pattern.CASE_INSENSITIVE)));
        }
        if (StringUtils.isNotBlank(bo.getProviderName())) {
            query.addCriteria(Criteria.where("providerName").regex(Pattern.compile(String.format("^.*%s.*$", bo.getProviderName()), Pattern.CASE_INSENSITIVE)));
        }
        return query;
    }

    @Override
    public PageResult<AigcProviderVo> page(PageQuery<AigcProviderBo> pageQuery) {
        PageQuery<Query> page = pageQuery.buildPage();
        page.setQuery(buildQuery(pageQuery.getQuery()));
        return this.selectPageVo(page);
    }

    @Override
    public AigcProviderVo detail(String id) {
        AigcProviderEntity entity = this.getById(id);
        return convertTo(entity);
    }

    @Override
    public String saveAigcProvider(AigcProviderBo bo) {
        AigcProviderEntity entity = this.save(bo);
        return null == entity ? null : entity.getId();
    }

    @Override
    public boolean updateAigcProvider(AigcProviderBo bo) {
        return this.patch(bo);
    }

    @Override
    public boolean deleteAigcProvider(List<String> ids) {
        return this.deleteByIds(ids);
    }

    @Override
    public Map<String, AigcProviderEntity> mapProvider() {
        List<AigcProviderEntity> list = this.selectList();
        return StreamUtils.toIdentityMap(list, AigcProviderEntity::getProviderCode);
    }

}