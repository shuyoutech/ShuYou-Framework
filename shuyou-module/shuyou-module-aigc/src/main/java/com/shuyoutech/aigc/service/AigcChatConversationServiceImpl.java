package com.shuyoutech.aigc.service;

import com.shuyoutech.aigc.domain.bo.AigcChatConversationBo;
import com.shuyoutech.aigc.domain.entity.AigcChatConversationEntity;
import com.shuyoutech.aigc.domain.vo.AigcChatConversationVo;
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
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author YangChao
 * @date 2025-07-20 17:08:25
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class AigcChatConversationServiceImpl implements AigcChatConversationService {

    public List<AigcChatConversationVo> convertTo(List<AigcChatConversationEntity> list) {
        List<AigcChatConversationVo> result = CollectionUtils.newArrayList();
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        list.forEach(e -> {
            AigcChatConversationVo vo = MapstructUtils.convert(e, AigcChatConversationVo.class);
            result.add(vo);
        });
        return result;
    }

    @Override
    public Query buildQuery(AigcChatConversationBo bo) {
        Query query = new Query();
        if (StringUtils.isNotBlank(bo.getTitle())) {
            query.addCriteria(Criteria.where("title").regex(Pattern.compile(String.format("^.*%s.*$", bo.getTitle()), Pattern.CASE_INSENSITIVE)));
        }
        if (StringUtils.isNotBlank(bo.getUserId())) {
            query.addCriteria(Criteria.where("userId").is(bo.getUserId()));
        }
        return query;
    }

    @Override
    public PageResult<AigcChatConversationVo> page(PageQuery<AigcChatConversationBo> pageQuery) {
        AigcChatConversationBo bo = pageQuery.getQuery();
        bo.setUserId(AuthUtils.getLoginUserId());
        Query query = buildQuery(bo);
        PageResult<AigcChatConversationVo> pageResult = PageResult.empty();
        long count = MongoUtils.count(query, AigcChatConversationEntity.class);
        if (0 == count) {
            return pageResult;
        }
        Pageable pageable = PageRequest.of(pageQuery.getPageNum() - 1, pageQuery.getPageSize());
        query.with(pageable);
        query.with(Sort.by(Sort.Direction.DESC, "createTime"));
        List<AigcChatConversationEntity> rows = MongoUtils.selectList(query, AigcChatConversationEntity.class);
        pageResult.setRows(this.convertTo(rows));
        pageResult.setTotal(pageResult.getTotal());
        return pageResult;
    }

    @Override
    public boolean updateAigcChatConversation(AigcChatConversationBo bo) {
        Update update = new Update();
        update.set("title", bo.getTitle());
        return MongoUtils.patch(bo.getId(), update, AigcChatConversationEntity.class);
    }

    @Override
    public boolean deleteAigcChatConversation(List<String> ids) {
        return MongoUtils.deleteByIds(ids, AigcChatConversationEntity.class) > 0;
    }

}