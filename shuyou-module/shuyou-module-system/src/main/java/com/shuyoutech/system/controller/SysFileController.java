package com.shuyoutech.system.controller;

import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.model.R;
import com.shuyoutech.system.domain.bo.SysFileBo;
import com.shuyoutech.system.domain.vo.SysFileVo;
import com.shuyoutech.system.service.SysFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.shuyoutech.common.core.model.R.success;

/**
 * @author YangChao
 * @date 2025-04-09 14:05
 **/
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("file")
@Tag(name = "SysFileController", description = "文件服务API接口")
public class SysFileController {

    @PostMapping("upload")
    @Operation(summary = "上传文件")
    public R<SysFileVo> upload(@RequestParam("file") MultipartFile file) {
        return success(sysFileService.upload(file));
    }

    @PostMapping("download/{ossId:.+}")
    @Operation(summary = "下载文件")
    public void down(@PathVariable String ossId, HttpServletRequest request, HttpServletResponse response) {
        sysFileService.down(ossId, request, response);
    }

    @PostMapping("delete/{ossId:.+}")
    @Operation(summary = "删除文件")
    public R<Void> delete(@PathVariable String ossId) {
        sysFileService.deleteFileById(ossId);
        return success();
    }

    @PostMapping(path = "getPreviewUrl/{ossId:.+}")
    @Operation(summary = "根据id获取预览url")
    public R<String> getPreviewUrl(@PathVariable String ossId) {
        return R.success(sysFileService.generatedUrl(ossId));
    }

    @PostMapping("page")
    @Operation(summary = "文件分页列表")
    public R<PageResult<SysFileVo>> page(@RequestBody PageQuery<SysFileBo> pageQuery) {
        return R.success(sysFileService.page(pageQuery));
    }

    private final SysFileService sysFileService;

}
