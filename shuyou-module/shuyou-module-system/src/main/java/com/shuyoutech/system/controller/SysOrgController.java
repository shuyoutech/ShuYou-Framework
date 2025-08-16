package com.shuyoutech.system.controller;

import cn.hutool.core.lang.tree.Tree;
import com.shuyoutech.common.core.model.*;
import com.shuyoutech.system.domain.bo.SysOrgBo;
import com.shuyoutech.system.domain.vo.SysOrgVo;
import com.shuyoutech.system.service.SysOrgService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author YangChao
 * @date 2025-07-07 09:17:49
 **/
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("org")
@Tag(name = "SysOrgController", description = "机构管理API控制器")
public class SysOrgController {

    @PostMapping("page")
    @Operation(description = "机构分页列表")
    public R<PageResult<SysOrgVo>> page(@RequestBody PageQuery<SysOrgBo> pageQuery) {
        return R.success(sysOrgService.page(pageQuery));
    }

    @PostMapping(path = "detail/{id}")
    @Operation(description = "查询机构详情")
    public R<SysOrgVo> detail(@PathVariable String id) {
        return R.success(sysOrgService.detail(id));
    }

    @PostMapping(path = "save")
    @Operation(description = "新增机构")
    public R<String> save(@Validated({SaveGroup.class}) @RequestBody SysOrgBo bo) {
        return R.success(sysOrgService.saveSysOrg(bo));
    }

    @PostMapping(path = "update")
    @Operation(description = "修改机构")
    public R<Boolean> update(@Validated({UpdateGroup.class}) @RequestBody SysOrgBo bo) {
        return R.success(sysOrgService.updateSysOrg(bo));
    }

    @PostMapping(path = "delete")
    @Operation(description = "删除机构")
    public R<Boolean> delete(@RequestBody List<String> ids) {
        return R.success(sysOrgService.deleteSysOrg(ids));
    }

    @PostMapping(path = "unique")
    @Operation(description = "检测唯一性")
    public R<Boolean> unique(@Validated @RequestBody ParamUnique param) {
        return R.success(sysOrgService.checkUnique(param));
    }

    @PostMapping(path = "status")
    @Operation(description = "状态修改")
    public R<Boolean> status(@Validated({StatusGroup.class}) @RequestBody SysOrgBo bo) {
        return R.success(sysOrgService.statusSysOrg(bo.getId(), bo.getStatus()));
    }

    @PostMapping("tree")
    @Operation(description = "获取树列表")
    public R<List<Tree<String>>> tree(@RequestBody SysOrgBo bo) {
        return R.success(sysOrgService.tree(bo));
    }

    private final SysOrgService sysOrgService;

}
