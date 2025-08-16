package com.shuyoutech.system.controller;

import cn.hutool.core.lang.tree.Tree;
import com.shuyoutech.common.core.model.*;
import com.shuyoutech.system.domain.bo.SysMenuBo;
import com.shuyoutech.system.domain.vo.SysMenuVo;
import com.shuyoutech.system.service.SysMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author YangChao
 * @date 2025-07-07 10:44:24
 **/
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("menu")
@Tag(name = "SysMenuController", description = "菜单管理API控制器")
public class SysMenuController {

    @PostMapping("page")
    @Operation(description = "菜单分页列表")
    public R<PageResult<SysMenuVo>> page(@RequestBody PageQuery<SysMenuBo> pageQuery) {
        return R.success(sysMenuService.page(pageQuery));
    }

    @PostMapping(path = "detail/{id}")
    @Operation(description = "查询菜单详情")
    public R<SysMenuVo> detail(@PathVariable String id) {
        return R.success(sysMenuService.detail(id));
    }

    @PostMapping(path = "save")
    @Operation(description = "新增菜单")
    public R<String> save(@Validated({SaveGroup.class}) @RequestBody SysMenuBo bo) {
        return R.success(sysMenuService.saveSysMenu(bo));
    }

    @PostMapping(path = "update")
    @Operation(description = "修改菜单")
    public R<Boolean> update(@Validated({UpdateGroup.class}) @RequestBody SysMenuBo bo) {
        return R.success(sysMenuService.updateSysMenu(bo));
    }

    @PostMapping(path = "delete")
    @Operation(description = "删除菜单")
    public R<Boolean> delete(@RequestBody List<String> ids) {
        return R.success(sysMenuService.deleteSysMenu(ids));
    }

    @PostMapping(path = "unique")
    @Operation(description = "检测唯一性")
    public R<Boolean> unique(@Validated @RequestBody ParamUnique param) {
        return R.success(sysMenuService.checkUnique(param));
    }

    @PostMapping(path = "status")
    @Operation(description = "状态修改")
    public R<Boolean> status(@Validated({StatusGroup.class}) @RequestBody SysMenuBo bo) {
        return R.success(sysMenuService.statusSysMenu(bo.getId(), bo.getStatus()));
    }

    @PostMapping("tree")
    @Operation(description = "获取菜单树列表")
    public R<List<Tree<String>>> tree(@RequestBody SysMenuBo bo) {
        return R.success(sysMenuService.tree(bo));
    }

    private final SysMenuService sysMenuService;

}
