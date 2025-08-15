package com.shuyoutech.common.core.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author YangChao
 * @date 2025-07-08 00:12
 **/
@Data
@Builder
@Schema(description = "树形对象")
public class TreeOption implements Serializable {

    @Schema(description = "父类ID")
    private String parentId;

    @Schema(description = "类型")
    private String type;

    @Schema(description = "显示文本")
    private String label;

    @Schema(description = "显示值")
    private String value;

    @Schema(description = "排序号")
    private Integer sort;

    @Schema(description = "额外数据")
    private Map<String, Object> extra;

    @Schema(description = "子集合")
    private List<TreeOption> children;

}
