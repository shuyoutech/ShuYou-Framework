package com.shuyoutech.aigc.controller;

import com.shuyoutech.aigc.domain.bo.AigcAppBo;
import com.shuyoutech.aigc.domain.bo.AigcChatCompletionsBo;
import com.shuyoutech.aigc.domain.bo.AigcChatTestBo;
import com.shuyoutech.aigc.domain.vo.AigcAppVo;
import com.shuyoutech.aigc.service.AigcAppService;
import com.shuyoutech.common.core.model.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.shuyoutech.common.core.constant.CommonConstants.*;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

/**
 * @author YangChao
 * @date 2025-07-12 09:29:09
 **/
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("app")
@Tag(name = "AigcAppController", description = "应用管理API控制器")
public class AigcAppController {

    @PostMapping("page")
    @Operation(description = "应用分页列表")
    public R<PageResult<AigcAppVo>> page(@RequestBody PageQuery<AigcAppBo> pageQuery) {
        return R.success(aigcAppService.page(pageQuery));
    }

    @PostMapping(path = "detail/{id}")
    @Operation(description = "查询应用详情")
    public R<AigcAppVo> detail(@PathVariable String id) {
        return R.success(aigcAppService.detail(id));
    }

    @PostMapping(path = "save")
    @Operation(description = "新增应用")
    public R<String> save(@Validated({SaveGroup.class}) @RequestBody AigcAppBo bo) {
        return R.success(aigcAppService.saveAiApp(bo));
    }

    @PostMapping(path = "update")
    @Operation(description = "修改应用")
    public R<Boolean> update(@Validated({UpdateGroup.class}) @RequestBody AigcAppBo bo) {
        return R.success(aigcAppService.updateAiApp(bo));
    }

    @PostMapping(path = "delete")
    @Operation(description = "删除应用")
    public R<Boolean> delete(@RequestBody List<String> ids) {
        return R.success(aigcAppService.deleteAiApp(ids));
    }

    @PostMapping(path = "unique")
    @Operation(description = "检测唯一性")
    public R<Boolean> unique(@Validated @RequestBody ParamUnique param) {
        return R.success(aigcAppService.checkUnique(param));
    }

    @PostMapping(value = "chatTest")
    @Operation(description = "对话聊天测试")
    public void chatTest(@RequestBody AigcChatTestBo bo, HttpServletResponse response) {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(TEXT_EVENT_STREAM_VALUE);
        response.setCharacterEncoding(CHARSET_UTF_8);
        response.setHeader(CACHE_CONTROL, NO_CACHE);
        aigcAppService.chatTest(bo, response);
    }

    @PostMapping(value = "chat/completions")
    @Operation(description = "对话聊天")
    public void completions(@RequestBody AigcChatCompletionsBo bo, HttpServletResponse response) {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(TEXT_EVENT_STREAM_VALUE);
        response.setCharacterEncoding(CHARSET_UTF_8);
        response.setHeader(CACHE_CONTROL, NO_CACHE);
        aigcAppService.completions(bo, response);
    }

    private final AigcAppService aigcAppService;

}
