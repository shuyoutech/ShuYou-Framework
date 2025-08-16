package com.shuyoutech.system.service;

import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.model.ParamUnique;
import com.shuyoutech.common.core.util.MapstructUtils;
import com.shuyoutech.common.core.util.StringUtils;
import com.shuyoutech.common.mongodb.MongoUtils;
import com.shuyoutech.common.web.service.SuperServiceImpl;
import com.shuyoutech.system.domain.bo.SysNoticeBo;
import com.shuyoutech.system.domain.entity.SysNoticeEntity;
import com.shuyoutech.system.domain.vo.SysNoticeVo;
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
 * @date 2025-07-07 10:55:00
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class SysNoticeServiceImpl extends SuperServiceImpl<SysNoticeEntity, SysNoticeVo> implements SysNoticeService {

    @Override
    public List<SysNoticeVo> convertTo(List<SysNoticeEntity> list) {
        return MapstructUtils.convert(list, this.voClass);
    }

    public SysNoticeVo convertTo(SysNoticeEntity entity) {
        return convertTo(Collections.singletonList(entity)).getFirst();
    }

    @Override
    public Query buildQuery(SysNoticeBo bo) {
        Query query = new Query();
        if (null != bo.getNoticeType()) {
            query.addCriteria(Criteria.where("noticeType").is(bo.getNoticeType()));
        }
        if (StringUtils.isNotBlank(bo.getNoticeTitle())) {
            query.addCriteria(Criteria.where("noticeTitle").regex(Pattern.compile(String.format("^.*%s.*$", bo.getNoticeTitle()), Pattern.CASE_INSENSITIVE)));
        }
        return query;
    }

    @Override
    public boolean checkUnique(ParamUnique paramUnique) {
        Query query = new Query();
        query.addCriteria(Criteria.where(paramUnique.getParamCode()).is(paramUnique.getParamValue()));
        SysNoticeEntity role = this.selectOne(query);
        if (null == role) {
            return true;
        }
        return StringUtils.isNotBlank(paramUnique.getId()) && paramUnique.getId().equals(role.getId());
    }

    @Override
    public PageResult<SysNoticeVo> page(PageQuery<SysNoticeBo> pageQuery) {
        PageQuery<Query> page = pageQuery.buildPage();
        page.setQuery(buildQuery(pageQuery.getQuery()));
        return this.selectPageVo(page);
    }

    @Override
    public SysNoticeVo detail(String id) {
        SysNoticeEntity entity = this.getById(id);
        return convertTo(entity);
    }

    @Override
    public String saveSysNotice(SysNoticeBo bo) {
        SysNoticeEntity entity = this.save(bo);
        return null == entity ? null : entity.getId();
    }

    @Override
    public boolean updateSysNotice(SysNoticeBo bo) {
        return this.patch(bo);
    }

    @Override
    public boolean deleteSysNotice(List<String> ids) {
        return this.deleteByIds(ids);
    }

    @Override
    public boolean statusSysNotice(String id, String status) {
        SysNoticeEntity entity = this.getById(id);
        if (null == entity) {
            return false;
        }
        Update update = new Update();
        update.set("status", status);
        return MongoUtils.patch(id, update, SysNoticeEntity.class);
    }

}