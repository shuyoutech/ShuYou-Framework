package com.shuyoutech.common.core.model;

import com.shuyoutech.common.core.enums.QuerySortEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author YangChao
 * @date 2025-04-06 20:49
 **/
@Data
@NoArgsConstructor
public class ParamSort implements Serializable {

    @Schema(description = "列名称")
    private String name;

    @Schema(description = "排序字段顺序 DESC、ASC")
    private String order;

    public ParamSort(String column) {
        this.name = column;
        this.order = QuerySortEnum.DESC.getValue();
    }

    public ParamSort(String column, QuerySortEnum order) {
        this.name = column;
        this.order = order.getValue();
    }

}
