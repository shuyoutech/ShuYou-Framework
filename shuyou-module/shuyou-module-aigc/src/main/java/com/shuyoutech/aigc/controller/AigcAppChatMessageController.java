package com.shuyoutech.aigc.controller;

import com.shuyoutech.aigc.domain.bo.AigcAppChatMessageBo;
import com.shuyoutech.aigc.domain.vo.AigcAppChatMessageVo;
import com.shuyoutech.aigc.service.AigcAppChatMessageService;
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
 * @date 2025-07-12 15:44:07
 **/
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("appChatMessage")
@Tag(name = "AigcAppChatMessageController", description = "app对话消息管理API控制器")
public class AigcAppChatMessageController {

    @PostMapping("page")
    @Operation(description = "对话消息分页列表")
    public R<PageResult<AigcAppChatMessageVo>> page(@RequestBody PageQuery<AigcAppChatMessageBo> pageQuery) {
        return R.success(aigcAppChatMessageService.page(pageQuery));
    }

    @PostMapping(path = "detail/{id}")
    @Operation(description = "查询对话消息详情")
    public R<AigcAppChatMessageVo> detail(@PathVariable String id) {
        return R.success(aigcAppChatMessageService.detail(id));
    }

    private final AigcAppChatMessageService aigcAppChatMessageService;

}
