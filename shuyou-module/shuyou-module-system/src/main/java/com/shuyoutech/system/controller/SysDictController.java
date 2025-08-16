package com.shuyoutech.system.controller;

import com.shuyoutech.common.core.model.*;
import com.shuyoutech.system.domain.bo.SysDictBo;
import com.shuyoutech.system.domain.vo.SysDictVo;
import com.shuyoutech.system.service.SysDictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author YangChao
 * @date 2025-07-07 10:17:54
 **/
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("dict")
@Tag(name = "SysDictController", description = "字典管理API控制器")
public class SysDictController {

    @PostMapping("page")
    @Operation(description = "字典分页列表")
    public R<PageResult<SysDictVo>> page(@RequestBody PageQuery<SysDictBo> pageQuery) {
        return R.success(sysDictService.page(pageQuery));
    }

    @PostMapping(path = "detail/{id}")
    @Operation(description = "查询字典详情")
    public R<SysDictVo> detail(@PathVariable String id) {
        return R.success(sysDictService.detail(id));
    }

    @PostMapping(path = "save")
    @Operation(description = "新增字典")
    public R<String> save(@Validated({SaveGroup.class}) @RequestBody SysDictBo bo) {
        return R.success(sysDictService.saveSysDict(bo));
    }

    @PostMapping(path = "update")
    @Operation(description = "修改字典")
    public R<Boolean> update(@Validated({UpdateGroup.class}) @RequestBody SysDictBo bo) {
        return R.success(sysDictService.updateSysDict(bo));
    }

    @PostMapping(path = "delete")
    @Operation(description = "删除字典")
    public R<Boolean> delete(@RequestBody List<String> ids) {
        return R.success(sysDictService.deleteSysDict(ids));
    }

    @PostMapping(path = "unique")
    @Operation(description = "检测唯一性")
    public R<Boolean> unique(@Validated @RequestBody ParamUnique param) {
        return R.success(sysDictService.checkUnique(param));
    }

    private final SysDictService sysDictService;

}
