package com.shuyoutech.system.controller;

import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.model.R;
import com.shuyoutech.system.domain.vo.SysLogQuery;
import com.shuyoutech.system.domain.vo.SysLogVo;
import com.shuyoutech.system.service.SysLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author YangChao
 * @date 2025-01-15 08:00
 **/
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("log")
@Tag(name = "SysLogController", description = "日志API控制器")
public class SysLogController {

    @PostMapping("page")
    @Operation(description = "日志分页列表")
    public R<PageResult<SysLogVo>> page(@RequestBody PageQuery<SysLogQuery> pageQuery) {
        return R.success(sysLogService.page(pageQuery));
    }

    @PostMapping(path = "detail/{id}")
    @Operation(description = "查询日志详情")
    public R<SysLogVo> detail(@PathVariable String id) {
        return R.success(sysLogService.detail(id));
    }

    private final SysLogService sysLogService;

}
