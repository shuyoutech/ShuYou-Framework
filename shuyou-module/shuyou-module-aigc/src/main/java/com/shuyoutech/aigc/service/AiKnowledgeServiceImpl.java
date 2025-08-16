package com.shuyoutech.aigc.service;

import com.alibaba.fastjson2.JSON;
import com.shuyoutech.aigc.constant.AiConstants;
import com.shuyoutech.aigc.domain.bo.AigcKnowledgeBo;
import com.shuyoutech.aigc.domain.entity.AigcKnowledgeEntity;
import com.shuyoutech.aigc.domain.entity.AigcModelEntity;
import com.shuyoutech.aigc.domain.entity.AigcVectorStoreEntity;
import com.shuyoutech.aigc.domain.vo.AigcKnowledgeVo;
import com.shuyoutech.common.cache.enums.CacheMsgTypeEnum;
import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.model.ParamUnique;
import com.shuyoutech.common.core.util.CollectionUtils;
import com.shuyoutech.common.core.util.MapstructUtils;
import com.shuyoutech.common.core.util.StreamUtils;
import com.shuyoutech.common.core.util.StringUtils;
import com.shuyoutech.common.redis.constant.CacheConstants;
import com.shuyoutech.common.redis.model.RedisMessage;
import com.shuyoutech.common.redis.util.RedisUtils;
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
 * @date 2025-07-11 15:01:17
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class AiKnowledgeServiceImpl extends SuperServiceImpl<AigcKnowledgeEntity, AigcKnowledgeVo> implements AiKnowledgeService {

    @Override
    public List<AigcKnowledgeVo> convertTo(List<AigcKnowledgeEntity> list) {
        List<AigcKnowledgeVo> result = CollectionUtils.newArrayList();
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        List<AigcModelEntity> modelList = aigcModelService.selectList();
        Map<String, AigcModelEntity> modelMap = StreamUtils.toIdentityMap(modelList, AigcModelEntity::getId);
        List<AigcVectorStoreEntity> vectorStoreList = aigcVectorStoreService.selectList();
        Map<String, AigcVectorStoreEntity> vectorStoreMap = StreamUtils.toIdentityMap(vectorStoreList, AigcVectorStoreEntity::getId);
        list.forEach(e -> {
            AigcKnowledgeVo vo = MapstructUtils.convert(e, this.voClass);
            if (null != vo) {
                vo.setVectorStore(vectorStoreMap.get(e.getVectorStoreId()));
                vo.setEmbeddingModel(modelMap.get(e.getEmbeddingModelId()));
                result.add(vo);
            }
        });
        return result;
    }

    public AigcKnowledgeVo convertTo(AigcKnowledgeEntity entity) {
        return convertTo(Collections.singletonList(entity)).getFirst();
    }

    @Override
    public Query buildQuery(AigcKnowledgeBo bo) {
        Query query = new Query();
        if (StringUtils.isNotBlank(bo.getKnowledgeName())) {
            query.addCriteria(Criteria.where("knowledgeName").regex(Pattern.compile(String.format("^.*%s.*$", bo.getKnowledgeName()), Pattern.CASE_INSENSITIVE)));
        }
        return query;
    }

    @Override
    public boolean checkUnique(ParamUnique paramUnique) {
        Query query = new Query();
        query.addCriteria(Criteria.where(paramUnique.getParamCode()).is(paramUnique.getParamValue()));
        AigcKnowledgeEntity role = this.selectOne(query);
        if (null == role) {
            return true;
        }
        return StringUtils.isNotBlank(paramUnique.getId()) && paramUnique.getId().equals(role.getId());
    }

    @Override
    public PageResult<AigcKnowledgeVo> page(PageQuery<AigcKnowledgeBo> pageQuery) {
        PageQuery<Query> page = pageQuery.buildPage();
        page.setQuery(buildQuery(pageQuery.getQuery()));
        return this.selectPageVo(page);
    }

    @Override
    public AigcKnowledgeVo detail(String id) {
        AigcKnowledgeEntity entity = this.getById(id);
        return convertTo(entity);
    }

    @Override
    public String saveAiKnowledge(AigcKnowledgeBo bo) {
        AigcKnowledgeEntity entity = this.save(bo);
        RedisUtils.convertAndSend(CacheConstants.SHUYOU_PATTERN_TOPIC, RedisMessage.of(AiConstants.KNOWLEDGE, CacheMsgTypeEnum.UPDATE.getValue(), entity.getId(), JSON.toJSONString(entity)));
        return entity.getId();
    }

    @Override
    public boolean updateAiKnowledge(AigcKnowledgeBo bo) {
        boolean patch = this.patch(bo);
        if (!patch) {
            return false;
        }
        AigcKnowledgeEntity entity = getById(bo.getId());
        if (null == entity) {
            return false;
        }
        RedisUtils.convertAndSend(CacheConstants.SHUYOU_PATTERN_TOPIC, RedisMessage.of(AiConstants.KNOWLEDGE, CacheMsgTypeEnum.UPDATE.getValue(), entity.getId(), JSON.toJSONString(entity)));
        return true;
    }

    @Override
    public boolean deleteAiKnowledge(List<String> ids) {
        boolean flag = this.deleteByIds(ids);
        if (!flag) {
            return false;
        }
        for (String id : ids) {
            RedisUtils.convertAndSend(CacheConstants.SHUYOU_PATTERN_TOPIC, RedisMessage.of(AiConstants.KNOWLEDGE, CacheMsgTypeEnum.DELETE.getValue(), id, ""));
        }
        return true;
    }

    private final AigcModelService aigcModelService;
    private final AigcVectorStoreService aigcVectorStoreService;

}