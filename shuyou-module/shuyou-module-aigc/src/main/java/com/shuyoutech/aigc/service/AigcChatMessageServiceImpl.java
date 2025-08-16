package com.shuyoutech.aigc.service;

import com.shuyoutech.aigc.domain.bo.AigcChatMessageBo;
import com.shuyoutech.aigc.domain.entity.AigcChatMessageEntity;
import com.shuyoutech.aigc.domain.vo.AigcChatMessageVo;
import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.util.CollectionUtils;
import com.shuyoutech.common.core.util.MapstructUtils;
import com.shuyoutech.common.core.util.StringUtils;
import com.shuyoutech.common.mongodb.MongoUtils;
import com.shuyoutech.common.satoken.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author YangChao
 * @date 2025-07-20 21:19:49
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class AigcChatMessageServiceImpl implements AigcChatMessageService {

    public List<AigcChatMessageVo> convertTo(List<AigcChatMessageEntity> list) {
        List<AigcChatMessageVo> result = CollectionUtils.newArrayList();
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        list.forEach(e -> {
            AigcChatMessageVo vo = MapstructUtils.convert(e, AigcChatMessageVo.class);
            result.add(vo);
        });
        return result;
    }

    @Override
    public Query buildQuery(AigcChatMessageBo bo) {
        Query query = new Query();
        if (StringUtils.isNotBlank(bo.getConversationId())) {
            query.addCriteria(Criteria.where("conversationId").is(bo.getConversationId()));
        }
        if (StringUtils.isNotBlank(bo.getUserId())) {
            query.addCriteria(Criteria.where("userId").is(bo.getUserId()));
        }
        return query;
    }

    @Override
    public PageResult<AigcChatMessageVo> page(PageQuery<AigcChatMessageBo> pageQuery) {
        AigcChatMessageBo bo = pageQuery.getQuery();
        bo.setUserId(AuthUtils.getLoginUserId());
        Query query = buildQuery(bo);
        PageResult<AigcChatMessageVo> pageResult = PageResult.empty();
        long count = MongoUtils.count(query, AigcChatMessageEntity.class);
        if (0 == count) {
            return pageResult;
        }
        Pageable pageable = PageRequest.of(pageQuery.getPageNum() - 1, pageQuery.getPageSize());
        query.with(pageable);
        query.with(Sort.by(Sort.Direction.DESC, "requestTime"));
        List<AigcChatMessageEntity> rows = MongoUtils.selectList(query, AigcChatMessageEntity.class);
        pageResult.setRows(this.convertTo(rows));
        pageResult.setTotal(pageResult.getTotal());
        return pageResult;
    }

}