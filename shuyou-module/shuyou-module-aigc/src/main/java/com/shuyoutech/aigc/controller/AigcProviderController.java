package com.shuyoutech.aigc.controller;

import com.shuyoutech.aigc.domain.bo.AigcProviderBo;
import com.shuyoutech.aigc.domain.vo.AigcProviderVo;
import com.shuyoutech.aigc.service.AigcProviderService;
import com.shuyoutech.common.core.model.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author YangChao
 * @date 2025-08-12 22:28:58
 **/
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("provider")
@Tag(name = "AigcProviderController", description = "供应商管理API控制器")
public class AigcProviderController {

    @PostMapping("page")
    @Operation(description = "供应商分页列表")
    public R<PageResult<AigcProviderVo>> page(@RequestBody PageQuery<AigcProviderBo> pageQuery) {
        return R.success(aigcProviderService.page(pageQuery));
    }

    @PostMapping(path = "detail/{id}")
    @Operation(description = "查询供应商详情")
    public R<AigcProviderVo> detail(@PathVariable String id) {
        return R.success(aigcProviderService.detail(id));
    }

    @PostMapping(path = "save")
    @Operation(description = "新增供应商")
    public R<String> save(@Validated({SaveGroup.class}) @RequestBody AigcProviderBo bo) {
        return R.success(aigcProviderService.saveAigcProvider(bo));
    }

    @PostMapping(path = "update")
    @Operation(description = "修改供应商")
    public R<Boolean> update(@Validated({UpdateGroup.class}) @RequestBody AigcProviderBo bo) {
        return R.success(aigcProviderService.updateAigcProvider(bo));
    }

    @PostMapping(path = "delete")
    @Operation(description = "删除供应商")
    public R<Boolean> delete(@RequestBody List<String> ids) {
        return R.success(aigcProviderService.deleteAigcProvider(ids));
    }

    private final AigcProviderService aigcProviderService;

}
