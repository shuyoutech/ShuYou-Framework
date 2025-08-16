package com.shuyoutech.aigc.service;

import com.shuyoutech.aigc.domain.bo.AigcImageMessageBo;
import com.shuyoutech.aigc.domain.entity.AigcImageMessageEntity;
import com.shuyoutech.aigc.domain.vo.AigcImageMessageVo;
import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.util.MapstructUtils;
import com.shuyoutech.common.web.service.SuperServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author YangChao
 * @date 2025-07-27 23:01:14
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class AigcImageMessageServiceImpl extends SuperServiceImpl<AigcImageMessageEntity, AigcImageMessageVo> implements AigcImageMessageService {

    @Override
    public List<AigcImageMessageVo> convertTo(List<AigcImageMessageEntity> list) {
        return MapstructUtils.convert(list, this.voClass);
    }

    public AigcImageMessageVo convertTo(AigcImageMessageEntity entity) {
        return convertTo(Collections.singletonList(entity)).getFirst();
    }

    @Override
    public Query buildQuery(AigcImageMessageBo bo) {
        Query query = new Query();
        return query;
    }

    @Override
    public PageResult<AigcImageMessageVo> page(PageQuery<AigcImageMessageBo> pageQuery) {
        PageQuery<Query> page = pageQuery.buildPage();
        page.setQuery(buildQuery(pageQuery.getQuery()));
        return this.selectPageVo(page);
    }

    @Override
    public AigcImageMessageVo detail(String id) {
        AigcImageMessageEntity entity = this.getById(id);
        return convertTo(entity);
    }

}