package com.shuyoutech.aigc.service;

import com.shuyoutech.aigc.constant.AiConstants;
import com.shuyoutech.aigc.domain.bo.AigcKnowledgeDocSliceBo;
import com.shuyoutech.aigc.domain.entity.AigcKnowledgeDocSliceEntity;
import com.shuyoutech.aigc.domain.vo.AigcKnowledgeDocSliceVo;
import com.shuyoutech.aigc.provider.AigcModelFactory;
import com.shuyoutech.aigc.provider.AigcVectorStoreFactory;
import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.util.CollectionUtils;
import com.shuyoutech.common.core.util.MapstructUtils;
import com.shuyoutech.common.core.util.StreamUtils;
import com.shuyoutech.common.core.util.StringUtils;
import com.shuyoutech.common.web.service.SuperServiceImpl;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author YangChao
 * @date 2025-07-11 19:38:09
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class AiKnowledgeDocSliceServiceImpl extends SuperServiceImpl<AigcKnowledgeDocSliceEntity, AigcKnowledgeDocSliceVo> implements AiKnowledgeDocSliceService {

    @Override
    public List<AigcKnowledgeDocSliceVo> convertTo(List<AigcKnowledgeDocSliceEntity> list) {
        return MapstructUtils.convert(list, this.voClass);
    }

    public AigcKnowledgeDocSliceVo convertTo(AigcKnowledgeDocSliceEntity entity) {
        return convertTo(Collections.singletonList(entity)).getFirst();
    }

    @Override
    public Query buildQuery(AigcKnowledgeDocSliceBo bo) {
        Query query = new Query();
        if (StringUtils.isNotBlank(bo.getKnowledgeId())) {
            query.addCriteria(Criteria.where("knowledgeId").is(bo.getKnowledgeId()));
        }
        if (StringUtils.isNotBlank(bo.getDocId())) {
            query.addCriteria(Criteria.where("docId").is(bo.getDocId()));
        }
        return query;
    }

    @Override
    public PageResult<AigcKnowledgeDocSliceVo> page(PageQuery<AigcKnowledgeDocSliceBo> pageQuery) {
        PageQuery<Query> page = pageQuery.buildPage();
        page.setQuery(buildQuery(pageQuery.getQuery()));
        return this.selectPageVo(page);
    }

    @Override
    public AigcKnowledgeDocSliceVo detail(String id) {
        AigcKnowledgeDocSliceEntity entity = this.getById(id);
        return convertTo(entity);
    }

    @Override
    public String saveAiKnowledgeDocSlice(AigcKnowledgeDocSliceBo bo) {
        AigcKnowledgeDocSliceEntity entity = this.save(bo);
        return null == entity ? null : entity.getId();
    }

    @Override
    public boolean updateAiKnowledgeDocSlice(AigcKnowledgeDocSliceBo bo) {
        TextSegment segment = TextSegment.from(bo.getContent(), Metadata.metadata(AiConstants.KNOWLEDGE_ID, bo.getKnowledgeId()).put(AiConstants.DOC_ID, bo.getDocId()));
        EmbeddingModel embeddingModel = aigcModelFactory.getEmbeddingModelByKnowledgeId(bo.getKnowledgeId());
        if (null == embeddingModel) {
            log.error("embeddingText embeddingModel is not exist KnowledgeId={}, DocId={}", bo.getKnowledgeId(), bo.getDocId());
            return false;
        }
        EmbeddingStore<TextSegment> embeddingStore = aigcVectorStoreFactory.getEmbeddingStoreByKnowledgeId(bo.getKnowledgeId());
        if (null == embeddingStore) {
            log.error("embeddingText embeddingStore is not exist KnowledgeId={}, DocId={}", bo.getKnowledgeId(), bo.getDocId());
            return false;
        }
        Embedding embedding = embeddingModel.embed(segment).content();
        embeddingStore.addAll(CollectionUtils.newArrayList(bo.getVectorId()), CollectionUtils.newArrayList(embedding), CollectionUtils.newArrayList(segment));
        return this.patch(bo);
    }

    @Override
    public boolean deleteAiKnowledgeDocSlice(List<String> ids) {
        List<AigcKnowledgeDocSliceEntity> sliceList = this.getByIds(ids);
        if (CollectionUtils.isEmpty(sliceList)) {
            return false;
        }
        EmbeddingStore<TextSegment> embeddingStore = aigcVectorStoreFactory.getEmbeddingStoreByKnowledgeId(sliceList.getFirst().getKnowledgeId());
        if (null == embeddingStore) {
            return false;
        }
        embeddingStore.removeAll(StreamUtils.toList(sliceList, AigcKnowledgeDocSliceEntity::getVectorId));
        return this.deleteByIds(ids);
    }

    private final AigcVectorStoreFactory aigcVectorStoreFactory;
    private final AigcModelFactory aigcModelFactory;

}