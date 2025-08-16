package com.shuyoutech.system.controller;

import com.shuyoutech.common.core.model.*;
import com.shuyoutech.system.domain.bo.SysConfigBo;
import com.shuyoutech.system.domain.vo.SysConfigVo;
import com.shuyoutech.system.service.SysConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author YangChao
 * @date 2025-07-07 10:39:22
 **/
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("config")
@Tag(name = "SysConfigController", description = "参数配置管理API控制器")
public class SysConfigController {

    @PostMapping("page")
    @Operation(description = "参数配置分页列表")
    public R<PageResult<SysConfigVo>> page(@RequestBody PageQuery<SysConfigBo> pageQuery) {
        return R.success(sysConfigService.page(pageQuery));
    }

    @PostMapping(path = "detail/{id}")
    @Operation(description = "查询参数配置详情")
    public R<SysConfigVo> detail(@PathVariable String id) {
        return R.success(sysConfigService.detail(id));
    }

    @PostMapping(path = "save")
    @Operation(description = "新增参数配置")
    public R<String> save(@Validated({SaveGroup.class}) @RequestBody SysConfigBo bo) {
        return R.success(sysConfigService.saveSysConfig(bo));
    }

    @PostMapping(path = "update")
    @Operation(description = "修改参数配置")
    public R<Boolean> update(@Validated({UpdateGroup.class}) @RequestBody SysConfigBo bo) {
        return R.success(sysConfigService.updateSysConfig(bo));
    }

    @PostMapping(path = "delete")
    @Operation(description = "删除参数配置")
    public R<Boolean> delete(@RequestBody List<String> ids) {
        return R.success(sysConfigService.deleteSysConfig(ids));
    }

    @PostMapping(path = "unique")
    @Operation(description = "检测唯一性")
    public R<Boolean> unique(@Validated @RequestBody ParamUnique param) {
        return R.success(sysConfigService.checkUnique(param));
    }

    private final SysConfigService sysConfigService;

}
