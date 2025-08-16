package com.shuyoutech.aigc.controller;

import com.shuyoutech.aigc.domain.bo.AigcAppChatConversationBo;
import com.shuyoutech.aigc.domain.vo.AigcAppChatConversationVo;
import com.shuyoutech.aigc.service.AigcAppChatConversationService;
import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.model.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author YangChao
 * @date 2025-07-12 14:52:04
 **/
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("appChatConversation")
@Tag(name = "AigcAppChatConversationController", description = "app对话窗口管理API控制器")
public class AigcAppChatConversationController {

    @PostMapping("page")
    @Operation(description = "对话窗口分页列表")
    public R<PageResult<AigcAppChatConversationVo>> page(@RequestBody PageQuery<AigcAppChatConversationBo> pageQuery) {
        return R.success(aigcAppChatConversationService.page(pageQuery));
    }

    @PostMapping(path = "detail/{id}")
    @Operation(description = "查询对话窗口详情")
    public R<AigcAppChatConversationVo> detail(@PathVariable String id) {
        return R.success(aigcAppChatConversationService.detail(id));
    }

    private final AigcAppChatConversationService aigcAppChatConversationService;

}
