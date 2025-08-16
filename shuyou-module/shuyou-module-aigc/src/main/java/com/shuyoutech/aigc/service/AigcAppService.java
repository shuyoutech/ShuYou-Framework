package com.shuyoutech.aigc.service;

import com.shuyoutech.aigc.domain.bo.AigcAppBo;
import com.shuyoutech.aigc.domain.bo.AigcChatCompletionsBo;
import com.shuyoutech.aigc.domain.bo.AigcChatTestBo;
import com.shuyoutech.aigc.domain.entity.AigcAppEntity;
import com.shuyoutech.aigc.domain.vo.AigcAppVo;
import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.model.ParamUnique;
import com.shuyoutech.common.web.service.SuperService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * @author YangChao
 * @date 2025-07-12 09:29:09
 **/
public interface AigcAppService extends SuperService<AigcAppEntity, AigcAppVo> {

    Query buildQuery(AigcAppBo bo);

    boolean checkUnique(ParamUnique paramUnique);

    PageResult<AigcAppVo> page(PageQuery<AigcAppBo> pageQuery);

    AigcAppVo detail(String id);

    String saveAiApp(AigcAppBo bo);

    boolean updateAiApp(AigcAppBo bo);

    boolean deleteAiApp(List<String> ids);

    void chatTest(AigcChatTestBo bo, HttpServletResponse response);

    void completions(AigcChatCompletionsBo bo, HttpServletResponse response);

}
