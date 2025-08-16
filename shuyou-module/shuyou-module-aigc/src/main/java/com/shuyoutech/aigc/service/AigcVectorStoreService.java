package com.shuyoutech.aigc.service;

import com.shuyoutech.aigc.domain.bo.AigcVectorStoreBo;
import com.shuyoutech.aigc.domain.entity.AigcVectorStoreEntity;
import com.shuyoutech.aigc.domain.vo.AigcVectorStoreVo;
import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.model.ParamUnique;
import com.shuyoutech.common.web.service.SuperService;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * @author YangChao
 * @date 2025-07-11 13:44:53
 **/
public interface AigcVectorStoreService extends SuperService<AigcVectorStoreEntity, AigcVectorStoreVo> {

    Query buildQuery(AigcVectorStoreBo bo);

    boolean checkUnique(ParamUnique paramUnique);

    PageResult<AigcVectorStoreVo> page(PageQuery<AigcVectorStoreBo> pageQuery);

    AigcVectorStoreVo detail(String id);

    String saveAiVectorStore(AigcVectorStoreBo bo);

    boolean updateAiVectorStore(AigcVectorStoreBo bo);

    boolean deleteAiVectorStore(List<String> ids);

    boolean testConnect(String id);

}
