package com.shuyoutech.system.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.shuyoutech.common.core.model.DropDownOptions;
import com.shuyoutech.common.core.model.R;
import com.shuyoutech.common.core.util.StreamUtils;
import com.shuyoutech.system.domain.vo.CommonTemplateFileVo;
import com.shuyoutech.system.domain.vo.SysDictDataVo;
import com.shuyoutech.system.service.CacheService;
import com.shuyoutech.system.service.CommonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author YangChao
 * @date 2025-01-15 14:37
 **/
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("common")
@Tag(name = "CommonController", description = "公共API控制器")
public class CommonController {

    @PostMapping("dict/options/{dictCode}")
    @Operation(description = "数据字典下拉框")
    public R<List<DropDownOptions>> dictOptions(@PathVariable String dictCode) {
        List<SysDictDataVo> itemList = cacheService.queryDictList(dictCode);
        if (CollectionUtil.isEmpty(itemList)) {
            return R.success(CollectionUtil.newArrayList());
        }
        List<DropDownOptions> list = StreamUtils.toList(itemList, item ->
                new DropDownOptions()
                        .label(item.getDictLabel())
                        .value(item.getDictValue())
                        .sort(item.getDictSort()));
        return R.success(list);
    }

    @PostMapping("templateFile")
    @Operation(description = "下载模板文件")
    public void templateFile(@Validated @RequestBody CommonTemplateFileVo data,//
                             HttpServletRequest request, HttpServletResponse response) {
        String templateType = data.getTemplateType();
        commonService.templateFile(templateType, request, response);
    }

    private final CacheService cacheService;
    private final CommonService commonService;

}
