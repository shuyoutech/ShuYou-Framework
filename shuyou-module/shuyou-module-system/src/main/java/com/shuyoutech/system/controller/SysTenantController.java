package com.shuyoutech.system.controller;

import com.shuyoutech.common.core.model.*;
import com.shuyoutech.system.domain.bo.SysTenantBo;
import com.shuyoutech.system.domain.vo.SysTenantVo;
import com.shuyoutech.system.service.SysTenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author YangChao
 * @date 2025-07-07 09:01:15
 **/
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("tenant")
@Tag(name = "SysTenantController", description = "租户管理API控制器")
public class SysTenantController {

    @PostMapping("page")
    @Operation(description = "租户分页列表")
    public R<PageResult<SysTenantVo>> page(@RequestBody PageQuery<SysTenantBo> pageQuery) {
        return R.success(sysTenantService.page(pageQuery));
    }

    @PostMapping(path = "detail/{id}")
    @Operation(description = "查询租户详情")
    public R<SysTenantVo> detail(@PathVariable String id) {
        return R.success(sysTenantService.detail(id));
    }

    @PostMapping(path = "save")
    @Operation(description = "新增租户")
    public R<String> save(@Validated({SaveGroup.class}) @RequestBody SysTenantBo bo) {
        return R.success(sysTenantService.saveSysTenant(bo));
    }

    @PostMapping(path = "update")
    @Operation(description = "修改租户")
    public R<Boolean> update(@Validated({UpdateGroup.class}) @RequestBody SysTenantBo bo) {
        return R.success(sysTenantService.updateSysTenant(bo));
    }

    @PostMapping(path = "delete")
    @Operation(description = "删除租户")
    public R<Boolean> delete(@RequestBody List<String> ids) {
        return R.success(sysTenantService.deleteSysTenant(ids));
    }

    @PostMapping(path = "unique")
    @Operation(description = "检测唯一性")
    public R<Boolean> unique(@Validated @RequestBody ParamUnique param) {
        return R.success(sysTenantService.checkUnique(param));
    }

    @PostMapping(path = "status")
    @Operation(description = "状态修改")
    public R<Boolean> status(@Validated({StatusGroup.class}) @RequestBody SysTenantBo bo) {
        return R.success(sysTenantService.statusSysTenant(bo.getId(), bo.getStatus()));
    }

    private final SysTenantService sysTenantService;

}
