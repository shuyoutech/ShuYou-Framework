package com.shuyoutech.aigc.service;

import com.shuyoutech.aigc.domain.bo.AigcAppChatConversationBo;
import com.shuyoutech.aigc.domain.entity.AigcAppChatConversationEntity;
import com.shuyoutech.aigc.domain.vo.AigcAppChatConversationVo;
import com.shuyoutech.api.service.RemoteSystemService;
import com.shuyoutech.common.core.enums.DictTypeEnum;
import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.util.CollectionUtils;
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
 * @date 2025-07-12 14:52:04
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class AigcAppChatConversationServiceImpl extends SuperServiceImpl<AigcAppChatConversationEntity, AigcAppChatConversationVo> implements AigcAppChatConversationService {

    @Override
    public List<AigcAppChatConversationVo> convertTo(List<AigcAppChatConversationEntity> list) {
        List<AigcAppChatConversationVo> result = CollectionUtils.newArrayList();
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        Map<String, String> sourceMap = remoteSystemService.translateByDictCode(DictTypeEnum.AI_SOURCE_TYPE.getValue());
        Map<String, String> userMap = remoteSystemService.getUserName(StreamUtils.toSet(list, AigcAppChatConversationEntity::getUserId));
        list.forEach(e -> {
            AigcAppChatConversationVo vo = MapstructUtils.convert(e, this.voClass);
            if (null != vo) {
                if (StringUtils.isNotBlank(vo.getSource())) {
                    vo.setSourceName(sourceMap.get(e.getSource()));
                }
                if (StringUtils.isNotBlank(e.getUserId())) {
                    vo.setUserName(userMap.get(e.getUserId()));
                }
                result.add(vo);
            }
        });
        return result;
    }

    public AigcAppChatConversationVo convertTo(AigcAppChatConversationEntity entity) {
        return convertTo(Collections.singletonList(entity)).getFirst();
    }

    @Override
    public Query buildQuery(AigcAppChatConversationBo bo) {
        Query query = new Query();
        if (StringUtils.isNotBlank(bo.getAppId())) {
            query.addCriteria(Criteria.where("appId").is(bo.getAppId()));
        }
        if (StringUtils.isNotBlank(bo.getSource())) {
            query.addCriteria(Criteria.where("source").is(bo.getSource()));
        }
        if (StringUtils.isNotBlank(bo.getTitle())) {
            query.addCriteria(Criteria.where("title").regex(Pattern.compile(String.format("^.*%s.*$", bo.getTitle()), Pattern.CASE_INSENSITIVE)));
        }
        if (null != bo.getStartDate() && null != bo.getEndDate()) {
            query.addCriteria(Criteria.where("createTime").gte(bo.getStartDate()).lte(bo.getEndDate()));
        }
        return query;
    }

    @Override
    public PageResult<AigcAppChatConversationVo> page(PageQuery<AigcAppChatConversationBo> pageQuery) {
        PageQuery<Query> page = pageQuery.buildPage();
        page.setQuery(buildQuery(pageQuery.getQuery()));
        return this.selectPageVo(page);
    }

    @Override
    public AigcAppChatConversationVo detail(String id) {
        AigcAppChatConversationEntity entity = this.getById(id);
        return convertTo(entity);
    }

    private final RemoteSystemService remoteSystemService;

}