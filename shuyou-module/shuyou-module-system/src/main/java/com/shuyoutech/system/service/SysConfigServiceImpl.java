package com.shuyoutech.system.service;

import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.model.ParamUnique;
import com.shuyoutech.common.core.util.MapstructUtils;
import com.shuyoutech.common.core.util.StringUtils;
import com.shuyoutech.common.web.service.SuperServiceImpl;
import com.shuyoutech.system.domain.bo.SysConfigBo;
import com.shuyoutech.system.domain.entity.SysConfigEntity;
import com.shuyoutech.system.domain.vo.SysConfigVo;
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
 * @date 2025-07-07 10:39:22
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl extends SuperServiceImpl<SysConfigEntity, SysConfigVo> implements SysConfigService {

    @Override
    public List<SysConfigVo> convertTo(List<SysConfigEntity> list) {
        return MapstructUtils.convert(list, this.voClass);
    }

    public SysConfigVo convertTo(SysConfigEntity entity) {
        return convertTo(Collections.singletonList(entity)).getFirst();
    }

    @Override
    public Query buildQuery(SysConfigBo bo) {
        Query query = new Query();
        if (StringUtils.isNotBlank(bo.getConfigName())) {
            query.addCriteria(Criteria.where("configName").regex(Pattern.compile(String.format("^.*%s.*$", bo.getConfigName()), Pattern.CASE_INSENSITIVE)));
        }
        if (StringUtils.isNotBlank(bo.getConfigKey())) {
            query.addCriteria(Criteria.where("configKey").regex(Pattern.compile(String.format("^.*%s.*$", bo.getConfigKey()), Pattern.CASE_INSENSITIVE)));
        }
        return query;
    }

    @Override
    public boolean checkUnique(ParamUnique paramUnique) {
        Query query = new Query();
        query.addCriteria(Criteria.where(paramUnique.getParamCode()).is(paramUnique.getParamValue()));
        SysConfigEntity role = this.selectOne(query);
        if (null == role) {
            return true;
        }
        return StringUtils.isNotBlank(paramUnique.getId()) && paramUnique.getId().equals(role.getId());
    }

    @Override
    public PageResult<SysConfigVo> page(PageQuery<SysConfigBo> pageQuery) {
        PageQuery<Query> page = pageQuery.buildPage();
        page.setQuery(buildQuery(pageQuery.getQuery()));
        return this.selectPageVo(page);
    }

    @Override
    public SysConfigVo detail(String id) {
        SysConfigEntity entity = this.getById(id);
        return convertTo(entity);
    }

    @Override
    public String saveSysConfig(SysConfigBo bo) {
        SysConfigEntity entity = this.save(bo);
        return null == entity ? null : entity.getId();
    }

    @Override
    public boolean updateSysConfig(SysConfigBo bo) {
        return this.patch(bo);
    }

    @Override
    public boolean deleteSysConfig(List<String> ids) {
        return this.deleteByIds(ids);
    }

}