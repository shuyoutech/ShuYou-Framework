package com.shuyoutech.aigc.controller;

import com.shuyoutech.aigc.domain.bo.AigcChatConversationBo;
import com.shuyoutech.aigc.domain.vo.AigcChatConversationVo;
import com.shuyoutech.aigc.service.AigcChatConversationService;
import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.model.R;
import com.shuyoutech.common.core.model.UpdateGroup;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author YangChao
 * @date 2025-07-20 17:08:25
 **/
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("chatConversation")
@Tag(name = "AigcChatConversationController", description = "对话窗口管理API控制器")
public class AigcChatConversationController {

    @PostMapping("page")
    @Operation(description = "对话窗口分页列表")
    public R<PageResult<AigcChatConversationVo>> page(@RequestBody PageQuery<AigcChatConversationBo> pageQuery) {
        return R.success(aigcChatConversationService.page(pageQuery));
    }

    @PostMapping(path = "update")
    @Operation(description = "修改对话窗口")
    public R<Boolean> update(@Validated({UpdateGroup.class}) @RequestBody AigcChatConversationBo bo) {
        return R.success(aigcChatConversationService.updateAigcChatConversation(bo));
    }

    @PostMapping(path = "delete")
    @Operation(description = "删除对话窗口")
    public R<Boolean> delete(@RequestBody List<String> ids) {
        return R.success(aigcChatConversationService.deleteAigcChatConversation(ids));
    }

    private final AigcChatConversationService aigcChatConversationService;

}
