package com.shuyoutech.aigc.controller;

import com.shuyoutech.aigc.domain.bo.AigcKnowledgeBo;
import com.shuyoutech.aigc.domain.vo.AigcKnowledgeVo;
import com.shuyoutech.aigc.service.AiKnowledgeService;
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
 * @date 2025-07-11 15:01:17
 **/
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("knowledge")
@Tag(name = "AiKnowledgeController", description = "知识库管理API控制器")
public class AiKnowledgeController {

    @PostMapping("page")
    @Operation(description = "知识库分页列表")
    public R<PageResult<AigcKnowledgeVo>> page(@RequestBody PageQuery<AigcKnowledgeBo> pageQuery) {
        return R.success(aiKnowledgeService.page(pageQuery));
    }

    @PostMapping(path = "detail/{id}")
    @Operation(description = "查询知识库详情")
    public R<AigcKnowledgeVo> detail(@PathVariable String id) {
        return R.success(aiKnowledgeService.detail(id));
    }

    @PostMapping(path = "save")
    @Operation(description = "新增知识库")
    public R<String> save(@Validated({SaveGroup.class}) @RequestBody AigcKnowledgeBo bo) {
        return R.success(aiKnowledgeService.saveAiKnowledge(bo));
    }

    @PostMapping(path = "update")
    @Operation(description = "修改知识库")
    public R<Boolean> update(@Validated({UpdateGroup.class}) @RequestBody AigcKnowledgeBo bo) {
        return R.success(aiKnowledgeService.updateAiKnowledge(bo));
    }

    @PostMapping(path = "delete")
    @Operation(description = "删除知识库")
    public R<Boolean> delete(@RequestBody List<String> ids) {
        return R.success(aiKnowledgeService.deleteAiKnowledge(ids));
    }

    @PostMapping(path = "unique")
    @Operation(description = "检测唯一性")
    public R<Boolean> unique(@Validated @RequestBody ParamUnique param) {
        return R.success(aiKnowledgeService.checkUnique(param));
    }

    private final AiKnowledgeService aiKnowledgeService;

}
