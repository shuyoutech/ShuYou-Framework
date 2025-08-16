package com.shuyoutech.aigc.controller;

import com.shuyoutech.aigc.domain.bo.AigcKnowledgeDocSliceBo;
import com.shuyoutech.aigc.domain.vo.AigcKnowledgeDocSliceVo;
import com.shuyoutech.aigc.service.AiKnowledgeDocSliceService;
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
 * @date 2025-07-11 19:38:09
 **/
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("knowledgeDocSlice")
@Tag(name = "AiKnowledgeDocSliceController", description = "知识库文档切片管理API控制器")
public class AiKnowledgeDocSliceController {

    @PostMapping("page")
    @Operation(description = "知识库文档切片分页列表")
    public R<PageResult<AigcKnowledgeDocSliceVo>> page(@RequestBody PageQuery<AigcKnowledgeDocSliceBo> pageQuery) {
        return R.success(aiKnowledgeDocSliceService.page(pageQuery));
    }

    @PostMapping(path = "detail/{id}")
    @Operation(description = "查询知识库文档切片详情")
    public R<AigcKnowledgeDocSliceVo> detail(@PathVariable String id) {
        return R.success(aiKnowledgeDocSliceService.detail(id));
    }

    @PostMapping(path = "save")
    @Operation(description = "新增知识库文档切片")
    public R<String> save(@Validated({SaveGroup.class}) @RequestBody AigcKnowledgeDocSliceBo bo) {
        return R.success(aiKnowledgeDocSliceService.saveAiKnowledgeDocSlice(bo));
    }

    @PostMapping(path = "update")
    @Operation(description = "修改知识库文档切片")
    public R<Boolean> update(@Validated({UpdateGroup.class}) @RequestBody AigcKnowledgeDocSliceBo bo) {
        return R.success(aiKnowledgeDocSliceService.updateAiKnowledgeDocSlice(bo));
    }

    @PostMapping(path = "delete")
    @Operation(description = "删除知识库文档切片")
    public R<Boolean> delete(@RequestBody List<String> ids) {
        return R.success(aiKnowledgeDocSliceService.deleteAiKnowledgeDocSlice(ids));
    }

    private final AiKnowledgeDocSliceService aiKnowledgeDocSliceService;

}
