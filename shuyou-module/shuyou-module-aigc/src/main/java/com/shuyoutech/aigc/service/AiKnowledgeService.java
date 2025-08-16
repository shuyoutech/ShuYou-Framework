package com.shuyoutech.aigc.service;

import com.shuyoutech.aigc.domain.bo.AigcKnowledgeBo;
import com.shuyoutech.aigc.domain.entity.AigcKnowledgeEntity;
import com.shuyoutech.aigc.domain.vo.AigcKnowledgeVo;
import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.model.ParamUnique;
import com.shuyoutech.common.web.service.SuperService;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * @author YangChao
 * @date 2025-07-11 15:01:17
 **/
public interface AiKnowledgeService extends SuperService<AigcKnowledgeEntity, AigcKnowledgeVo> {

    Query buildQuery(AigcKnowledgeBo bo);

    boolean checkUnique(ParamUnique paramUnique);

    PageResult<AigcKnowledgeVo> page(PageQuery<AigcKnowledgeBo> pageQuery);

    AigcKnowledgeVo detail(String id);

    String saveAiKnowledge(AigcKnowledgeBo bo);

    boolean updateAiKnowledge(AigcKnowledgeBo bo);

    boolean deleteAiKnowledge(List<String> ids);

}
