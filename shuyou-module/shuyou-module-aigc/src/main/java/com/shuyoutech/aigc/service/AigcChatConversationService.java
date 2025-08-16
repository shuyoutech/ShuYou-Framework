package com.shuyoutech.aigc.service;

import com.shuyoutech.aigc.domain.bo.AigcChatConversationBo;
import com.shuyoutech.aigc.domain.vo.AigcChatConversationVo;
import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * @author YangChao
 * @date 2025-07-20 17:08:25
 **/
public interface AigcChatConversationService {

    Query buildQuery(AigcChatConversationBo bo);

    PageResult<AigcChatConversationVo> page(PageQuery<AigcChatConversationBo> pageQuery);

    boolean updateAigcChatConversation(AigcChatConversationBo bo);

    boolean deleteAigcChatConversation(List<String> ids);

}
