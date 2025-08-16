package com.shuyoutech.aigc.controller;

import com.shuyoutech.aigc.domain.bo.ChatModelBo;
import com.shuyoutech.aigc.domain.bo.CommonModelBo;
import com.shuyoutech.aigc.service.AigcService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author YangChao
 * @date 2025-07-13 15:37
 **/
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
@Tag(name = "AigcController", description = "AI应用 API控制器")
public class AigcController {

    @PostMapping(value = "chat")
    @Operation(summary = "对话接口")
    public void chat(@RequestBody ChatModelBo bo, HttpServletResponse response) {
        aigcService.chat(bo, response);
    }

    @PostMapping(value = "image")
    @Operation(summary = "图片接口")
    public void image(@RequestBody CommonModelBo bo, HttpServletResponse response) {
        aigcService.model(bo, response);
    }

    @PostMapping(value = "audio")
    @Operation(summary = "音频接口")
    public void audio(@RequestBody CommonModelBo bo, HttpServletResponse response) {
        aigcService.model(bo, response);
    }

    @PostMapping(value = "video")
    @Operation(summary = "视频接口")
    public void video(@RequestBody CommonModelBo bo, HttpServletResponse response) {
        aigcService.model(bo, response);
    }

    private final AigcService aigcService;

}
