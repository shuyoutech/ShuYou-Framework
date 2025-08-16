package com.shuyoutech.aigc.service;

import com.shuyoutech.aigc.domain.bo.AigcChatMessageBo;
import com.shuyoutech.aigc.domain.vo.AigcChatMessageVo;
import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import org.springframework.data.mongodb.core.query.Query;

/**
 * @author YangChao
 * @date 2025-07-20 21:19:49
 **/
public interface AigcChatMessageService {

    Query buildQuery(AigcChatMessageBo bo);

    PageResult<AigcChatMessageVo> page(PageQuery<AigcChatMessageBo> pageQuery);

}
