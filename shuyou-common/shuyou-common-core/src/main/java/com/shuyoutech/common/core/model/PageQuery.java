package com.shuyoutech.common.core.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * @author YangChao
 * @date 2025-04-06 16:02
 **/
@Data
@Schema(description = "分页参数对象")
public class PageQuery<T> implements Serializable {

    @Schema(description = "当前页码", example = "1")
    private int pageNum = 1;

    @Schema(description = "每页记录条数", example = "10")
    private int pageSize = 10;

    @Schema(description = "排序字段", example = "id,createTime")
    private String sort;

    @Schema(description = "排序规则", example = "asc,desc")
    private String order;

    @Valid
    @NotNull(message = "查询参数对象不能为空")
    @Schema(description = "查询参数对象")
    private T query;

    public <Q> PageQuery<Q> buildPage() {
        PageQuery<Q> page = new PageQuery<>();
        page.setPageNum(pageNum);
        page.setPageSize(pageSize);
        page.setSort(sort);
        page.setOrder(order);
        return page;
    }

}
