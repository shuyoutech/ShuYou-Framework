package com.shuyoutech.system.service;

import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.model.ParamUnique;
import com.shuyoutech.common.core.util.MapstructUtils;
import com.shuyoutech.common.core.util.StringUtils;
import com.shuyoutech.common.web.service.SuperServiceImpl;
import com.shuyoutech.system.domain.bo.SysUserBusinessBo;
import com.shuyoutech.system.domain.entity.SysUserBusinessEntity;
import com.shuyoutech.system.domain.vo.SysUserBusinessVo;
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
 * @date 2025-07-10 13:35:30
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserBusinessServiceImpl extends SuperServiceImpl<SysUserBusinessEntity, SysUserBusinessVo> implements SysUserBusinessService {

    @Override
    public List<SysUserBusinessVo> convertTo(List<SysUserBusinessEntity> list) {
        return MapstructUtils.convert(list, this.voClass);
    }

    public SysUserBusinessVo convertTo(SysUserBusinessEntity entity) {
        return convertTo(Collections.singletonList(entity)).getFirst();
    }

    @Override
    public Query buildQuery(SysUserBusinessBo bo) {
        Query query = new Query();
        if (StringUtils.isNotBlank(bo.getUserId())) {
            query.addCriteria(Criteria.where("userId").regex(Pattern.compile(String.format("^.*%s.*$", bo.getUserId()), Pattern.CASE_INSENSITIVE)));
        }
        if (StringUtils.isNotBlank(bo.getBusinessType())) {
            query.addCriteria(Criteria.where("businessType").is(bo.getBusinessType()));
        }
        return query;
    }

    @Override
    public boolean checkUnique(ParamUnique paramUnique) {
        Query query = new Query();
        query.addCriteria(Criteria.where(paramUnique.getParamCode()).is(paramUnique.getParamValue()));
        SysUserBusinessEntity role = this.selectOne(query);
        if (null == role) {
            return true;
        }
        return StringUtils.isNotBlank(paramUnique.getId()) && paramUnique.getId().equals(role.getId());
    }

    @Override
    public PageResult<SysUserBusinessVo> page(PageQuery<SysUserBusinessBo> pageQuery) {
        PageQuery<Query> page = pageQuery.buildPage();
        page.setQuery(buildQuery(pageQuery.getQuery()));
        return this.selectPageVo(page);
    }

    @Override
    public SysUserBusinessVo detail(String id) {
        SysUserBusinessEntity entity = this.getById(id);
        return convertTo(entity);
    }

    @Override
    public String saveSysUserBusiness(SysUserBusinessBo bo) {
        SysUserBusinessEntity entity = this.save(bo);
        return null == entity ? null : entity.getId();
    }

    @Override
    public boolean updateSysUserBusiness(SysUserBusinessBo bo) {
        return this.patch(bo);
    }

    @Override
    public boolean deleteSysUserBusiness(List<String> ids) {
        return this.deleteByIds(ids);
    }

}