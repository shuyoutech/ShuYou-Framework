package com.shuyoutech.aigc.service;

import com.shuyoutech.aigc.domain.bo.AigcAppChatMessageBo;
import com.shuyoutech.aigc.domain.entity.AigcAppChatMessageEntity;
import com.shuyoutech.aigc.domain.vo.AigcAppChatMessageVo;
import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.web.service.SuperService;
import org.springframework.data.mongodb.core.query.Query;

/**
 * @author YangChao
 * @date 2025-07-12 15:44:07
 **/
public interface AigcAppChatMessageService extends SuperService<AigcAppChatMessageEntity, AigcAppChatMessageVo> {

    Query buildQuery(AigcAppChatMessageBo bo);

    PageResult<AigcAppChatMessageVo> page(PageQuery<AigcAppChatMessageBo> pageQuery);

    AigcAppChatMessageVo detail(String id);

}
