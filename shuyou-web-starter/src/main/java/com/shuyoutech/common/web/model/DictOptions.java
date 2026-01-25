package com.shuyoutech.common.web.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author YangChao
 * @since 2025-07-07 13:54
 **/
@Data
@Builder
@Schema(description = "数据字典选项对象")
public class DictOptions implements Serializable {

    @Schema(description = "父级ID")
    private String parentId;

    @Schema(description = "父级值")
    private String parentValue;

    @Schema(description = "ID")
    private String id;

    @Schema(description = "字典编码")
    private String code;

    @Schema(description = "显示文本")
    private String label;

    @Schema(description = "显示值")
    private String value;

    @Schema(description = "排序号")
    private Integer sort;

    @Schema(description = "图标")
    private String icon;

}
