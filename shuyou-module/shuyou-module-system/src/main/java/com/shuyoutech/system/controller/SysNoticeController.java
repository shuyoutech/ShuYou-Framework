package com.shuyoutech.system.controller;

import com.shuyoutech.common.core.model.*;
import com.shuyoutech.system.domain.bo.SysNoticeBo;
import com.shuyoutech.system.domain.vo.SysNoticeVo;
import com.shuyoutech.system.service.SysNoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author YangChao
 * @date 2025-07-07 10:55:00
 **/
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("notice")
@Tag(name = "SysNoticeController", description = "通知公告管理API控制器")
public class SysNoticeController {

    @PostMapping("page")
    @Operation(description = "通知公告分页列表")
    public R<PageResult<SysNoticeVo>> page(@RequestBody PageQuery<SysNoticeBo> pageQuery) {
        return R.success(sysNoticeService.page(pageQuery));
    }

    @PostMapping(path = "detail/{id}")
    @Operation(description = "查询通知公告详情")
    public R<SysNoticeVo> detail(@PathVariable String id) {
        return R.success(sysNoticeService.detail(id));
    }

    @PostMapping(path = "save")
    @Operation(description = "新增通知公告")
    public R<String> save(@Validated({SaveGroup.class}) @RequestBody SysNoticeBo bo) {
        return R.success(sysNoticeService.saveSysNotice(bo));
    }

    @PostMapping(path = "update")
    @Operation(description = "修改通知公告")
    public R<Boolean> update(@Validated({UpdateGroup.class}) @RequestBody SysNoticeBo bo) {
        return R.success(sysNoticeService.updateSysNotice(bo));
    }

    @PostMapping(path = "delete")
    @Operation(description = "删除通知公告")
    public R<Boolean> delete(@RequestBody List<String> ids) {
        return R.success(sysNoticeService.deleteSysNotice(ids));
    }

    @PostMapping(path = "unique")
    @Operation(description = "检测唯一性")
    public R<Boolean> unique(@Validated @RequestBody ParamUnique param) {
        return R.success(sysNoticeService.checkUnique(param));
    }

    @PostMapping(path = "status")
    @Operation(description = "状态修改")
    public R<Boolean> status(@Validated({StatusGroup.class}) @RequestBody SysNoticeBo bo) {
        return R.success(sysNoticeService.statusSysNotice(bo.getId(), bo.getStatus()));
    }

    private final SysNoticeService sysNoticeService;

}
