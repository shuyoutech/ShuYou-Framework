package com.shuyoutech.aigc.service;

import com.shuyoutech.aigc.domain.bo.AigcAppChatConversationBo;
import com.shuyoutech.aigc.domain.entity.AigcAppChatConversationEntity;
import com.shuyoutech.aigc.domain.vo.AigcAppChatConversationVo;
import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.web.service.SuperService;
import org.springframework.data.mongodb.core.query.Query;

/**
 * @author YangChao
 * @date 2025-07-12 14:52:04
 **/
public interface AigcAppChatConversationService extends SuperService<AigcAppChatConversationEntity, AigcAppChatConversationVo> {

    Query buildQuery(AigcAppChatConversationBo bo);

    PageResult<AigcAppChatConversationVo> page(PageQuery<AigcAppChatConversationBo> pageQuery);

    AigcAppChatConversationVo detail(String id);

}
