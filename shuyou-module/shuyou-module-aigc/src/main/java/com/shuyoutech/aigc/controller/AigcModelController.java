package com.shuyoutech.aigc.controller;

import com.shuyoutech.aigc.domain.bo.AigcModelBo;
import com.shuyoutech.aigc.domain.vo.AigcModelVo;
import com.shuyoutech.aigc.service.AigcModelService;
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
 * @date 2025-07-11 11:18:54
 **/
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("model")
@Tag(name = "AiModelController", description = "模型管理API控制器")
public class AigcModelController {

    @PostMapping("page")
    @Operation(description = "模型分页列表")
    public R<PageResult<AigcModelVo>> page(@RequestBody PageQuery<AigcModelBo> pageQuery) {
        return R.success(aigcModelService.page(pageQuery));
    }

    @PostMapping(path = "detail/{id}")
    @Operation(description = "查询模型详情")
    public R<AigcModelVo> detail(@PathVariable String id) {
        return R.success(aigcModelService.detail(id));
    }

    @PostMapping(path = "save")
    @Operation(description = "新增模型")
    public R<String> save(@Validated({SaveGroup.class}) @RequestBody AigcModelBo bo) {
        return R.success(aigcModelService.saveAiModel(bo));
    }

    @PostMapping(path = "update")
    @Operation(description = "修改模型")
    public R<Boolean> update(@Validated({UpdateGroup.class}) @RequestBody AigcModelBo bo) {
        return R.success(aigcModelService.updateAiModel(bo));
    }

    @PostMapping(path = "delete")
    @Operation(description = "删除模型")
    public R<Boolean> delete(@RequestBody List<String> ids) {
        return R.success(aigcModelService.deleteAiModel(ids));
    }

    @PostMapping(path = "unique")
    @Operation(description = "检测唯一性")
    public R<Boolean> unique(@Validated @RequestBody ParamUnique param) {
        return R.success(aigcModelService.checkUnique(param));
    }

    @PostMapping(path = "status")
    @Operation(description = "状态修改")
    public R<Boolean> status(@Validated({StatusGroup.class}) @RequestBody AigcModelBo bo) {
        return R.success(aigcModelService.statusAiModel(bo.getId(), bo.getStatus()));
    }

    private final AigcModelService aigcModelService;

}
