package com.shuyoutech.common.web.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author YangChao
 * @since 2025-07-07 13:54
 **/
@Data
@Accessors(fluent = true)
@Schema(description = "下拉选项对象")
public class DropDownOptions implements Serializable {

    @Schema(description = "显示值")
    private String value;

    @Schema(description = "显示文本")
    private String label;

    @Schema(description = "排序号")
    private Integer sort;

}
