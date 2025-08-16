package com.shuyoutech.system.controller;

import com.shuyoutech.common.core.model.*;
import com.shuyoutech.system.domain.bo.SysDictDataBo;
import com.shuyoutech.system.domain.vo.SysDictDataVo;
import com.shuyoutech.system.service.SysDictDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author YangChao
 * @date 2025-07-07 10:02:47
 **/
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("dictData")
@Tag(name = "SysDictDataController", description = "字典数据管理API控制器")
public class SysDictDataController {

    @PostMapping("page")
    @Operation(description = "字典数据分页列表")
    public R<PageResult<SysDictDataVo>> page(@RequestBody PageQuery<SysDictDataBo> pageQuery) {
        return R.success(sysDictDataService.page(pageQuery));
    }

    @PostMapping(path = "detail/{id}")
    @Operation(description = "查询字典数据详情")
    public R<SysDictDataVo> detail(@PathVariable String id) {
        return R.success(sysDictDataService.detail(id));
    }

    @PostMapping(path = "save")
    @Operation(description = "新增字典数据")
    public R<String> save(@Validated({SaveGroup.class}) @RequestBody SysDictDataBo bo) {
        return R.success(sysDictDataService.saveSysDictData(bo));
    }

    @PostMapping(path = "update")
    @Operation(description = "修改字典数据")
    public R<Boolean> update(@Validated({UpdateGroup.class}) @RequestBody SysDictDataBo bo) {
        return R.success(sysDictDataService.updateSysDictData(bo));
    }

    @PostMapping(path = "delete")
    @Operation(description = "删除字典数据")
    public R<Boolean> delete(@RequestBody List<String> ids) {
        return R.success(sysDictDataService.deleteSysDictData(ids));
    }

    @PostMapping(path = "unique")
    @Operation(description = "检测唯一性")
    public R<Boolean> unique(@Validated @RequestBody ParamUnique param) {
        return R.success(sysDictDataService.checkUnique(param));
    }

    private final SysDictDataService sysDictDataService;

}
