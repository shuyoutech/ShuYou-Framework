package com.shuyoutech.aigc.provider;

import com.shuyoutech.aigc.domain.entity.AigcKnowledgeEntity;
import com.shuyoutech.aigc.domain.vo.AigcKnowledgeVo;
import com.shuyoutech.aigc.service.AiKnowledgeService;
import com.shuyoutech.common.core.util.MapstructUtils;
import com.shuyoutech.common.core.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.shuyoutech.aigc.provider.AigcModelFactory.MODEL_MAP;
import static com.shuyoutech.aigc.provider.AigcVectorStoreFactory.VECTOR_DB_MAP;

/**
 * @author YangChao
 * @date 2025-05-13 16:48
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class AigcKnowledgeFactory {

    public static final Map<String, AigcKnowledgeVo> KNOWLEDGE_MAP = new ConcurrentHashMap<>();

    public void init() {
        List<AigcKnowledgeEntity> knowledgeList = aiKnowledgeService.selectList();
        if (CollectionUtils.isEmpty(knowledgeList)) {
            return;
        }
        knowledgeList.forEach(this::buildKnowledge);
    }

    public void buildKnowledge(AigcKnowledgeEntity knowledge) {
        AigcKnowledgeVo vo = MapstructUtils.convert(knowledge, AigcKnowledgeVo.class);
        if (vo != null) {
            KNOWLEDGE_MAP.put(vo.getId(), vo);
        }
    }

    public AigcKnowledgeVo getKnowledge(String knowledgeId) {
        AigcKnowledgeVo knowledge = KNOWLEDGE_MAP.get(knowledgeId);
        if (null == knowledge) {
            return null;
        }
        if (StringUtils.isNotBlank(knowledge.getVectorStoreId())) {
            knowledge.setVectorStore(VECTOR_DB_MAP.get(knowledge.getVectorStoreId()));
        }
        if (StringUtils.isNotBlank(knowledge.getEmbeddingModelId())) {
            knowledge.setEmbeddingModel(MODEL_MAP.get(knowledge.getEmbeddingModelId()));
        }
        return knowledge;
    }

    private final AiKnowledgeService aiKnowledgeService;

}
