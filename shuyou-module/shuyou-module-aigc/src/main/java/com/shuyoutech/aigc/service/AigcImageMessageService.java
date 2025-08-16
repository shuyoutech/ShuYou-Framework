package com.shuyoutech.aigc.service;

import com.shuyoutech.aigc.domain.bo.AigcImageMessageBo;
import com.shuyoutech.aigc.domain.entity.AigcImageMessageEntity;
import com.shuyoutech.aigc.domain.vo.AigcImageMessageVo;
import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.web.service.SuperService;
import org.springframework.data.mongodb.core.query.Query;

/**
 * @author YangChao
 * @date 2025-07-27 23:01:14
 **/
public interface AigcImageMessageService extends SuperService<AigcImageMessageEntity, AigcImageMessageVo> {

    Query buildQuery(AigcImageMessageBo bo);

    PageResult<AigcImageMessageVo> page(PageQuery<AigcImageMessageBo> pageQuery);

    AigcImageMessageVo detail(String id);

}
