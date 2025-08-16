package com.shuyoutech.aigc.service;

import com.shuyoutech.aigc.domain.bo.AigcKnowledgeDocBo;
import com.shuyoutech.aigc.domain.entity.AigcKnowledgeDocEntity;
import com.shuyoutech.aigc.domain.vo.AigcKnowledgeDocVo;
import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.web.service.SuperService;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * @author YangChao
 * @date 2025-07-11 19:13:02
 **/
public interface AiKnowledgeDocService extends SuperService<AigcKnowledgeDocEntity, AigcKnowledgeDocVo> {

    Query buildQuery(AigcKnowledgeDocBo bo);

    PageResult<AigcKnowledgeDocVo> page(PageQuery<AigcKnowledgeDocBo> pageQuery);

    AigcKnowledgeDocVo detail(String id);

    String saveAiKnowledgeDoc(AigcKnowledgeDocBo bo);

    boolean updateAiKnowledgeDoc(AigcKnowledgeDocBo bo);

    boolean deleteAiKnowledgeDoc(List<String> ids);

}
