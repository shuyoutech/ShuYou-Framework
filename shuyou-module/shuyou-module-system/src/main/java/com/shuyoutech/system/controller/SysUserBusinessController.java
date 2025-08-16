package com.shuyoutech.system.controller;

import com.shuyoutech.common.core.model.*;
import com.shuyoutech.system.domain.bo.SysUserBusinessBo;
import com.shuyoutech.system.domain.vo.SysUserBusinessVo;
import com.shuyoutech.system.service.SysUserBusinessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author YangChao
 * @date 2025-07-10 13:35:30
 **/
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("userBusiness")
@Tag(name = "SysUserBusinessController", description = "用户业务管理API控制器")
public class SysUserBusinessController {

    @PostMapping("page")
    @Operation(description = "用户业务分页列表")
    public R<PageResult<SysUserBusinessVo>> page(@RequestBody PageQuery<SysUserBusinessBo> pageQuery) {
        return R.success(sysUserBusinessService.page(pageQuery));
    }

    @PostMapping(path = "detail/{id}")
    @Operation(description = "查询用户业务详情")
    public R<SysUserBusinessVo> detail(@PathVariable String id) {
        return R.success(sysUserBusinessService.detail(id));
    }

    @PostMapping(path = "save")
    @Operation(description = "新增用户业务")
    public R<String> save(@Validated({SaveGroup.class}) @RequestBody SysUserBusinessBo bo) {
        return R.success(sysUserBusinessService.saveSysUserBusiness(bo));
    }

    @PostMapping(path = "update")
    @Operation(description = "修改用户业务")
    public R<Boolean> update(@Validated({UpdateGroup.class}) @RequestBody SysUserBusinessBo bo) {
        return R.success(sysUserBusinessService.updateSysUserBusiness(bo));
    }

    @PostMapping(path = "delete")
    @Operation(description = "删除用户业务")
    public R<Boolean> delete(@RequestBody List<String> ids) {
        return R.success(sysUserBusinessService.deleteSysUserBusiness(ids));
    }

    @PostMapping(path = "unique")
    @Operation(description = "检测唯一性")
    public R<Boolean> unique(@Validated @RequestBody ParamUnique param) {
        return R.success(sysUserBusinessService.checkUnique(param));
    }

    private final SysUserBusinessService sysUserBusinessService;

}
