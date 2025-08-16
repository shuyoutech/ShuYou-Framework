package com.shuyoutech.system.controller;

import com.shuyoutech.common.core.model.*;
import com.shuyoutech.system.domain.bo.SysPostBo;
import com.shuyoutech.system.domain.vo.SysPostVo;
import com.shuyoutech.system.service.SysPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author YangChao
 * @date 2025-07-07 10:33:09
 **/
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("post")
@Tag(name = "SysPostController", description = "岗位管理API控制器")
public class SysPostController {

    @PostMapping("page")
    @Operation(description = "岗位分页列表")
    public R<PageResult<SysPostVo>> page(@RequestBody PageQuery<SysPostBo> pageQuery) {
        return R.success(sysPostService.page(pageQuery));
    }

    @PostMapping(path = "detail/{id}")
    @Operation(description = "查询岗位详情")
    public R<SysPostVo> detail(@PathVariable String id) {
        return R.success(sysPostService.detail(id));
    }

    @PostMapping(path = "save")
    @Operation(description = "新增岗位")
    public R<String> save(@Validated({SaveGroup.class}) @RequestBody SysPostBo bo) {
        return R.success(sysPostService.saveSysPost(bo));
    }

    @PostMapping(path = "update")
    @Operation(description = "修改岗位")
    public R<Boolean> update(@Validated({UpdateGroup.class}) @RequestBody SysPostBo bo) {
        return R.success(sysPostService.updateSysPost(bo));
    }

    @PostMapping(path = "delete")
    @Operation(description = "删除岗位")
    public R<Boolean> delete(@RequestBody List<String> ids) {
        return R.success(sysPostService.deleteSysPost(ids));
    }

    @PostMapping(path = "unique")
    @Operation(description = "检测唯一性")
    public R<Boolean> unique(@Validated @RequestBody ParamUnique param) {
        return R.success(sysPostService.checkUnique(param));
    }

    @PostMapping(path = "status")
    @Operation(description = "状态修改")
    public R<Boolean> status(@Validated({StatusGroup.class}) @RequestBody SysPostBo bo) {
        return R.success(sysPostService.statusSysPost(bo.getId(), bo.getStatus()));
    }

    private final SysPostService sysPostService;

}
