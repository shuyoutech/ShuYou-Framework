package com.shuyoutech.aigc.service;

import com.shuyoutech.aigc.domain.bo.AigcAppChatMessageBo;
import com.shuyoutech.aigc.domain.entity.AigcAppChatMessageEntity;
import com.shuyoutech.aigc.domain.vo.AigcAppChatMessageVo;
import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.util.MapstructUtils;
import com.shuyoutech.common.core.util.StringUtils;
import com.shuyoutech.common.web.service.SuperServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author YangChao
 * @date 2025-07-12 15:44:07
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class AigcAppChatMessageServiceImpl extends SuperServiceImpl<AigcAppChatMessageEntity, AigcAppChatMessageVo> implements AigcAppChatMessageService {

    @Override
    public List<AigcAppChatMessageVo> convertTo(List<AigcAppChatMessageEntity> list) {
        return MapstructUtils.convert(list, this.voClass);
    }

    public AigcAppChatMessageVo convertTo(AigcAppChatMessageEntity entity) {
        return convertTo(Collections.singletonList(entity)).getFirst();
    }

    @Override
    public Query buildQuery(AigcAppChatMessageBo bo) {
        Query query = new Query();
        if (StringUtils.isNotBlank(bo.getAppId())) {
            query.addCriteria(Criteria.where("appId").is(bo.getAppId()));
        }
        if (StringUtils.isNotBlank(bo.getConversationId())) {
            query.addCriteria(Criteria.where("conversationId").is(bo.getConversationId()));
        }
        return query;
    }

    @Override
    public PageResult<AigcAppChatMessageVo> page(PageQuery<AigcAppChatMessageBo> pageQuery) {
        PageQuery<Query> page = pageQuery.buildPage();
        page.setQuery(buildQuery(pageQuery.getQuery()));
        return this.selectPageVo(page);
    }

    @Override
    public AigcAppChatMessageVo detail(String id) {
        AigcAppChatMessageEntity entity = this.getById(id);
        return convertTo(entity);
    }

}