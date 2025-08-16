package com.shuyoutech.aigc.controller;

import com.shuyoutech.aigc.domain.bo.AigcVectorStoreBo;
import com.shuyoutech.aigc.domain.vo.AigcVectorStoreVo;
import com.shuyoutech.aigc.service.AigcVectorStoreService;
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
 * @date 2025-07-11 13:44:53
 **/
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("vectorStore")
@Tag(name = "AigcVectorStoreController", description = "向量数据库管理API控制器")
public class AigcVectorStoreController {

    @PostMapping("page")
    @Operation(description = "向量数据库分页列表")
    public R<PageResult<AigcVectorStoreVo>> page(@RequestBody PageQuery<AigcVectorStoreBo> pageQuery) {
        return R.success(aiVectorStoreService.page(pageQuery));
    }

    @PostMapping(path = "detail/{id}")
    @Operation(description = "查询向量数据库详情")
    public R<AigcVectorStoreVo> detail(@PathVariable String id) {
        return R.success(aiVectorStoreService.detail(id));
    }

    @PostMapping(path = "save")
    @Operation(description = "新增向量数据库")
    public R<String> save(@Validated({SaveGroup.class}) @RequestBody AigcVectorStoreBo bo) {
        return R.success(aiVectorStoreService.saveAiVectorStore(bo));
    }

    @PostMapping(path = "update")
    @Operation(description = "修改向量数据库")
    public R<Boolean> update(@Validated({UpdateGroup.class}) @RequestBody AigcVectorStoreBo bo) {
        return R.success(aiVectorStoreService.updateAiVectorStore(bo));
    }

    @PostMapping(path = "delete")
    @Operation(description = "删除向量数据库")
    public R<Boolean> delete(@RequestBody List<String> ids) {
        return R.success(aiVectorStoreService.deleteAiVectorStore(ids));
    }

    @PostMapping(path = "unique")
    @Operation(description = "检测唯一性")
    public R<Boolean> unique(@Validated @RequestBody ParamUnique param) {
        return R.success(aiVectorStoreService.checkUnique(param));
    }

    @PostMapping(path = "testConnect/{id}")
    @Operation(description = "测试连接数据库是否正常")
    public R<Boolean> testConnect(@PathVariable String id) {
        return R.success(aiVectorStoreService.testConnect(id));
    }

    private final AigcVectorStoreService aiVectorStoreService;

}
