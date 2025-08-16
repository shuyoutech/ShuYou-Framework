package com.shuyoutech.aigc.service;

import com.shuyoutech.aigc.domain.bo.AigcKnowledgeDocBo;
import com.shuyoutech.aigc.domain.bo.AigcKnowledgeDocSliceBo;
import com.shuyoutech.aigc.domain.entity.AigcKnowledgeDocEntity;
import com.shuyoutech.aigc.domain.entity.AigcKnowledgeDocSliceEntity;
import com.shuyoutech.aigc.domain.vo.AigcKnowledgeDocVo;
import com.shuyoutech.aigc.provider.AigcVectorStoreFactory;
import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.util.CollectionUtils;
import com.shuyoutech.common.core.util.MapstructUtils;
import com.shuyoutech.common.core.util.StreamUtils;
import com.shuyoutech.common.core.util.StringUtils;
import com.shuyoutech.common.web.service.SuperServiceImpl;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
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
 * @date 2025-07-11 19:13:02
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class AiKnowledgeDocServiceImpl extends SuperServiceImpl<AigcKnowledgeDocEntity, AigcKnowledgeDocVo> implements AiKnowledgeDocService {

    @Override
    public List<AigcKnowledgeDocVo> convertTo(List<AigcKnowledgeDocEntity> list) {
        return MapstructUtils.convert(list, this.voClass);
    }

    public AigcKnowledgeDocVo convertTo(AigcKnowledgeDocEntity entity) {
        return convertTo(Collections.singletonList(entity)).getFirst();
    }

    @Override
    public Query buildQuery(AigcKnowledgeDocBo bo) {
        Query query = new Query();
        if (StringUtils.isNotBlank(bo.getKnowledgeId())) {
            query.addCriteria(Criteria.where("knowledgeId").is(bo.getKnowledgeId()));
        }
        if (StringUtils.isNotBlank(bo.getDocName())) {
            query.addCriteria(Criteria.where("docName").regex(Pattern.compile(String.format("^.*%s.*$", bo.getDocName()), Pattern.CASE_INSENSITIVE)));
        }
        return query;
    }

    @Override
    public PageResult<AigcKnowledgeDocVo> page(PageQuery<AigcKnowledgeDocBo> pageQuery) {
        PageQuery<Query> page = pageQuery.buildPage();
        page.setQuery(buildQuery(pageQuery.getQuery()));
        return this.selectPageVo(page);
    }

    @Override
    public AigcKnowledgeDocVo detail(String id) {
        AigcKnowledgeDocEntity entity = this.getById(id);
        return convertTo(entity);
    }

    @Override
    public String saveAiKnowledgeDoc(AigcKnowledgeDocBo bo) {
        AigcKnowledgeDocEntity entity = this.save(bo);
        return null == entity ? null : entity.getId();
    }

    @Override
    public boolean updateAiKnowledgeDoc(AigcKnowledgeDocBo bo) {
        return this.patch(bo);
    }

    @Override
    public boolean deleteAiKnowledgeDoc(List<String> ids) {
        List<AigcKnowledgeDocEntity> docList = this.getByIds(ids);
        if (CollectionUtils.isEmpty(docList)) {
            return false;
        }
        AigcKnowledgeDocSliceBo sliceBo;
        List<AigcKnowledgeDocSliceEntity> sliceList;
        EmbeddingStore<TextSegment> embeddingStore;
        for (AigcKnowledgeDocEntity doc : docList) {
            sliceBo = new AigcKnowledgeDocSliceBo();
            sliceBo.setDocId(doc.getId());
            sliceList = aiKnowledgeDocSliceService.selectList(aiKnowledgeDocSliceService.buildQuery(sliceBo));
            if (CollectionUtils.isEmpty(sliceList)) {
                continue;
            }
            embeddingStore = aiVectorStoreFactory.getEmbeddingStoreByKnowledgeId(doc.getKnowledgeId());
            if (null == embeddingStore) {
                continue;
            }
            embeddingStore.removeAll(StreamUtils.toList(sliceList, AigcKnowledgeDocSliceEntity::getVectorId));
            aiKnowledgeDocSliceService.deleteByIds(StreamUtils.toList(sliceList, AigcKnowledgeDocSliceEntity::getId));
        }
        return deleteByIds(ids);
    }

    private final AiKnowledgeDocSliceService aiKnowledgeDocSliceService;
    private final AigcVectorStoreFactory aiVectorStoreFactory;

}