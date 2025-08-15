package com.shuyoutech.common.core.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author YangChao
 * @date 2025-04-06 15:52
 **/
@Data
@NoArgsConstructor
@Schema(description = "表格分页数据对象")
public class PageResult<T> implements Serializable {

    @Schema(description = "总记录数")
    private long total = 0;

    @Schema(description = "列表数据集合")
    private List<T> rows;

    public static <T> PageResult<T> empty() {
        return new PageResult<>(0, Collections.emptyList());
    }

    public static <T> PageResult<T> build(long total, List<T> list) {
        return new PageResult<>(total, list);
    }

    public PageResult(long total, List<T> list) {
        this.total = total;
        this.rows = list;
    }

}
