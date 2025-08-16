package com.shuyoutech.aigc.controller;

import com.shuyoutech.aigc.domain.bo.AigcImageMessageBo;
import com.shuyoutech.aigc.domain.vo.AigcImageMessageVo;
import com.shuyoutech.aigc.service.AigcImageMessageService;
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
 * @date 2025-07-27 23:01:14
 **/
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("imageMessage")
@Tag(name = "AigcImageMessageController", description = "绘画消息管理API控制器")
public class AigcImageMessageController {

    @PostMapping("page")
    @Operation(description = "绘画消息分页列表")
    public R<PageResult<AigcImageMessageVo>> page(@RequestBody PageQuery<AigcImageMessageBo> pageQuery) {
        return R.success(aigcImageMessageService.page(pageQuery));
    }

    @PostMapping(path = "detail/{id}")
    @Operation(description = "查询绘画消息详情")
    public R<AigcImageMessageVo> detail(@PathVariable String id) {
        return R.success(aigcImageMessageService.detail(id));
    }

    private final AigcImageMessageService aigcImageMessageService;

}
