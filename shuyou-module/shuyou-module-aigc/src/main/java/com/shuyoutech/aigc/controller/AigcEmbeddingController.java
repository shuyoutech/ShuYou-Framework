package com.shuyoutech.aigc.controller;

import com.shuyoutech.aigc.domain.vo.EmbeddingFileReq;
import com.shuyoutech.aigc.domain.vo.EmbeddingSearchReq;
import com.shuyoutech.aigc.domain.vo.EmbeddingTextReq;
import com.shuyoutech.aigc.service.AiEmbeddingService;
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
 * @date 2025-05-12 14:23:37
 **/
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("embedding")
@Tag(name = "AigcEmbeddingController", description = "文档向量化解析API控制器")
public class AigcEmbeddingController {

    @PostMapping(path = "text")
    @Operation(description = "导入知识库文本")
    public R<Void> text(@RequestBody EmbeddingTextReq req) {
        aiEmbeddingService.embeddingText(req);
        return R.success();
    }

    @PostMapping(path = "file")
    @Operation(description = "导入知识库文档")
    public R<Void> file(@RequestBody EmbeddingFileReq req) {
        aiEmbeddingService.embeddingFile(req);
        return R.success();
    }

    @PostMapping(path = "search")
    @Operation(description = "检索知识库文档")
    public R<Object> search(@RequestBody EmbeddingSearchReq req) {
        return R.success(aiEmbeddingService.embeddingSearch(req));
    }

    private final AiEmbeddingService aiEmbeddingService;

}
