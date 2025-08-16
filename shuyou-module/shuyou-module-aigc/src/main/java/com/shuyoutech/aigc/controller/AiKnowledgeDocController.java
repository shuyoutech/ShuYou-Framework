package com.shuyoutech.aigc.controller;

import com.shuyoutech.aigc.domain.bo.AigcKnowledgeDocBo;
import com.shuyoutech.aigc.domain.vo.AigcKnowledgeDocVo;
import com.shuyoutech.aigc.service.AiKnowledgeDocService;
import com.shuyoutech.common.core.model.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author YangChao
 * @date 2025-07-11 19:13:02
 **/
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("knowledgeDoc")
@Tag(name = "AiKnowledgeDocController", description = "知识库文档管理API控制器")
public class AiKnowledgeDocController {

    @PostMapping("page")
    @Operation(description = "知识库文档分页列表")
    public R<PageResult<AigcKnowledgeDocVo>> page(@RequestBody PageQuery<AigcKnowledgeDocBo> pageQuery) {
        return R.success(aiKnowledgeDocService.page(pageQuery));
    }

    @PostMapping(path = "detail/{id}")
    @Operation(description = "查询知识库文档详情")
    public R<AigcKnowledgeDocVo> detail(@PathVariable String id) {
        return R.success(aiKnowledgeDocService.detail(id));
    }

    @PostMapping(path = "save")
    @Operation(description = "新增知识库文档")
    public R<String> save(@Validated({SaveGroup.class}) @RequestBody AigcKnowledgeDocBo bo) {
        return R.success(aiKnowledgeDocService.saveAiKnowledgeDoc(bo));
    }

    @PostMapping(path = "update")
    @Operation(description = "修改知识库文档")
    public R<Boolean> update(@Validated({UpdateGroup.class}) @RequestBody AigcKnowledgeDocBo bo) {
        return R.success(aiKnowledgeDocService.updateAiKnowledgeDoc(bo));
    }

    @PostMapping(path = "delete")
    @Operation(description = "删除知识库文档")
    public R<Boolean> delete(@RequestBody List<String> ids) {
        return R.success(aiKnowledgeDocService.deleteAiKnowledgeDoc(ids));
    }

    private final AiKnowledgeDocService aiKnowledgeDocService;

}
