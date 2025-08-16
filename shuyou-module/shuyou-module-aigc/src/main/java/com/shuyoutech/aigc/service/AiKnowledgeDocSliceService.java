package com.shuyoutech.aigc.service;

import com.shuyoutech.aigc.domain.bo.AigcKnowledgeDocSliceBo;
import com.shuyoutech.aigc.domain.entity.AigcKnowledgeDocSliceEntity;
import com.shuyoutech.aigc.domain.vo.AigcKnowledgeDocSliceVo;
import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.web.service.SuperService;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * @author YangChao
 * @date 2025-07-11 19:38:09
 **/
public interface AiKnowledgeDocSliceService extends SuperService<AigcKnowledgeDocSliceEntity, AigcKnowledgeDocSliceVo> {

    Query buildQuery(AigcKnowledgeDocSliceBo bo);

    PageResult<AigcKnowledgeDocSliceVo> page(PageQuery<AigcKnowledgeDocSliceBo> pageQuery);

    AigcKnowledgeDocSliceVo detail(String id);

    String saveAiKnowledgeDocSlice(AigcKnowledgeDocSliceBo bo);

    boolean updateAiKnowledgeDocSlice(AigcKnowledgeDocSliceBo bo);

    boolean deleteAiKnowledgeDocSlice(List<String> ids);

}
