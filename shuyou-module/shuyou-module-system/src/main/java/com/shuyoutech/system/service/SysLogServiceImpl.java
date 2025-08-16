package com.shuyoutech.system.service;

import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.util.MapstructUtils;
import com.shuyoutech.common.core.util.StringUtils;
import com.shuyoutech.common.web.service.SuperServiceImpl;
import com.shuyoutech.system.domain.entity.SysLogEntity;
import com.shuyoutech.system.domain.vo.SysLogQuery;
import com.shuyoutech.system.domain.vo.SysLogVo;
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
 * @date 2025-07-14 17:32
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class SysLogServiceImpl extends SuperServiceImpl<SysLogEntity, SysLogVo> implements SysLogService {

    private Query buildQuery(SysLogQuery queryParam) {
        Query query = new Query();
        if (StringUtils.isNotBlank(queryParam.getRoleCode())) {
            query.addCriteria(Criteria.where("roleCode").regex(Pattern.compile(String.format("^.*%s.*$", queryParam.getRoleCode()), Pattern.CASE_INSENSITIVE)));
        }
        if (StringUtils.isNotBlank(queryParam.getRoleName())) {
            query.addCriteria(Criteria.where("roleName").regex(Pattern.compile(String.format("^.*%s.*$", queryParam.getRoleName()), Pattern.CASE_INSENSITIVE)));
        }
        return query;
    }

    @Override
    public List<SysLogVo> convertTo(List<SysLogEntity> list) {
        return MapstructUtils.convert(list, this.voClass);
    }

    public SysLogVo convertTo(SysLogEntity entity) {
        return convertTo(Collections.singletonList(entity)).getFirst();
    }

    @Override
    public PageResult<SysLogVo> page(PageQuery<SysLogQuery> pageQuery) {
        PageQuery<Query> page = pageQuery.buildPage();
        page.setQuery(buildQuery(pageQuery.getQuery()));
        return this.selectPageVo(page);
    }

    @Override
    public SysLogVo detail(String id) {
        SysLogEntity entity = this.getById(id);
        return convertTo(entity);
    }

}
