package com.shuyoutech.system.controller;

import com.shuyoutech.common.core.model.*;
import com.shuyoutech.system.domain.bo.SysDictTypeBo;
import com.shuyoutech.system.domain.vo.SysDictTypeVo;
import com.shuyoutech.system.service.SysDictTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author YangChao
 * @date 2025-07-07 09:55:16
 **/
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("dictType")
@Tag(name = "SysDictTypeController", description = "字典类型管理API控制器")
public class SysDictTypeController {

    @PostMapping("page")
    @Operation(description = "字典类型分页列表")
    public R<PageResult<SysDictTypeVo>> page(@RequestBody PageQuery<SysDictTypeBo> pageQuery) {
        return R.success(sysDictTypeService.page(pageQuery));
    }

    @PostMapping(path = "detail/{id}")
    @Operation(description = "查询字典类型详情")
    public R<SysDictTypeVo> detail(@PathVariable String id) {
        return R.success(sysDictTypeService.detail(id));
    }

    @PostMapping(path = "save")
    @Operation(description = "新增字典类型")
    public R<String> save(@Validated({SaveGroup.class}) @RequestBody SysDictTypeBo bo) {
        return R.success(sysDictTypeService.saveSysDictType(bo));
    }

    @PostMapping(path = "update")
    @Operation(description = "修改字典类型")
    public R<Boolean> update(@Validated({UpdateGroup.class}) @RequestBody SysDictTypeBo bo) {
        return R.success(sysDictTypeService.updateSysDictType(bo));
    }

    @PostMapping(path = "delete")
    @Operation(description = "删除字典类型")
    public R<Boolean> delete(@RequestBody List<String> ids) {
        return R.success(sysDictTypeService.deleteSysDictType(ids));
    }

    @PostMapping(path = "unique")
    @Operation(description = "检测唯一性")
    public R<Boolean> unique(@Validated @RequestBody ParamUnique param) {
        return R.success(sysDictTypeService.checkUnique(param));
    }

    private final SysDictTypeService sysDictTypeService;

}
