package com.shuyoutech.system.controller;

import com.shuyoutech.common.core.model.*;
import com.shuyoutech.system.domain.bo.*;
import com.shuyoutech.system.domain.vo.SysRoleVo;
import com.shuyoutech.system.domain.vo.SysUserVo;
import com.shuyoutech.system.service.CommonService;
import com.shuyoutech.system.service.SysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author YangChao
 * @date 2025-01-15 08:00
 **/
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("role")
@Tag(name = "SysRoleController", description = "角色API控制器")
public class SysRoleController {

    @PostMapping("page")
    @Operation(description = "角色分页列表")
    public R<PageResult<SysRoleVo>> page(@RequestBody PageQuery<SysRoleBo> pageQuery) {
        return R.success(sysRoleService.page(pageQuery));
    }

    @PostMapping(path = "detail/{id}")
    @Operation(description = "查询角色详情")
    public R<SysRoleVo> detail(@PathVariable String id) {
        return R.success(sysRoleService.detail(id));
    }

    @PostMapping(path = "save")
    @Operation(description = "新增角色")
    public R<String> save(@Validated({SaveGroup.class}) @RequestBody SysRoleBo bo) {
        if (!sysRoleService.checkUnique(ParamUnique.builder().paramCode("roleCode").paramValue(bo.getRoleCode()).build())) {
            return R.error("新增角色'" + bo.getRoleName() + "'失败，角色编码已存在");
        } else if (!sysRoleService.checkUnique(ParamUnique.builder().paramCode("roleName").paramValue(bo.getRoleName()).build())) {
            return R.error("新增角色'" + bo.getRoleName() + "'失败，角色名称已存在");
        }
        return R.success(sysRoleService.saveRole(bo));
    }

    @PostMapping(path = "update")
    @Operation(description = "修改角色")
    public R<Boolean> update(@Validated({UpdateGroup.class}) @RequestBody SysRoleBo bo) {
        if (!sysRoleService.checkUnique(ParamUnique.builder().id(bo.getId()).paramCode("roleCode").paramValue(bo.getRoleCode()).build())) {
            return R.error("修改角色'" + bo.getRoleName() + "'失败，角色编码已存在");
        } else if (!sysRoleService.checkUnique(ParamUnique.builder().id(bo.getId()).paramCode("roleName").paramValue(bo.getRoleName()).build())) {
            return R.error("修改角色'" + bo.getRoleName() + "'失败，角色名称已存在");
        }
        return R.success(sysRoleService.updateRole(bo));
    }

    @PostMapping(path = "delete")
    @Operation(description = "删除角色")
    public R<Boolean> delete(@RequestBody List<String> ids) {
        return R.success(sysRoleService.deleteRole(ids));
    }

    @PostMapping(path = "unique")
    @Operation(description = "检测唯一性")
    public R<Boolean> unique(@Validated @RequestBody ParamUnique param) {
        return R.success(sysRoleService.checkUnique(param));
    }

    @PostMapping(path = "status")
    @Operation(description = "状态修改")
    public R<Boolean> status(@Validated({StatusGroup.class}) @RequestBody SysRoleBo bo) {
        return R.success(sysRoleService.statusRole(bo.getId(), bo.getStatus()));
    }

    @PostMapping("grantUserList")
    @Operation(description = "查询已授权用户列表")
    public R<PageResult<SysUserVo>> grantUserList(@RequestBody PageQuery<SysUserBo> pageQuery) {
        return R.success(commonService.grantUserList(pageQuery));
    }

    @PostMapping("unGrantUserList")
    @Operation(description = "查询未授权用户列表")
    public R<PageResult<SysUserVo>> unGrantUserList(@RequestBody PageQuery<SysUserBo> pageQuery) {
        return R.success(commonService.unGrantUserList(pageQuery));
    }

    @PostMapping("cancelGrantUser")
    @Operation(description = "取消授权用户")
    public R<Void> cancelGrantUser(@RequestBody RoleUserBo bo) {
        commonService.cancelGrantUser(bo);
        return R.success();
    }

    @PostMapping("batchCancelGrantUser")
    @Operation(description = "批量取消授权用户")
    public R<Void> batchCancelGrantUser(@RequestBody RoleUserListBo bo) {
        commonService.batchCancelGrantUser(bo);
        return R.success();
    }

    @PostMapping("batchGrantUser")
    @Operation(description = "批量选择用户授权")
    public R<Void> batchGrantUser(@RequestBody RoleUserListBo bo) {
        commonService.batchGrantUser(bo);
        return R.success();
    }

    @PostMapping("grantMenu")
    @Operation(description = "给角色授权菜单")
    public R<Void> grantMenu(@RequestBody RoleMenuBo bo) {
        commonService.grantMenu(bo);
        return R.success();
    }

    private final SysRoleService sysRoleService;
    private final CommonService commonService;

}
