package com.shuyoutech.aigc.controller;

import com.shuyoutech.aigc.domain.bo.AigcChatMessageBo;
import com.shuyoutech.aigc.domain.vo.AigcChatMessageVo;
import com.shuyoutech.aigc.service.AigcChatMessageService;
import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.model.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author YangChao
 * @date 2025-07-20 21:19:49
 **/
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("chatMessage")
@Tag(name = "AigcChatMessageController", description = "对话消息管理API控制器")
public class AigcChatMessageController {

    @PostMapping("page")
    @Operation(description = "对话消息分页列表")
    public R<PageResult<AigcChatMessageVo>> page(@RequestBody PageQuery<AigcChatMessageBo> pageQuery) {
        return R.success(aigcChatMessageService.page(pageQuery));
    }

    private final AigcChatMessageService aigcChatMessageService;

}
