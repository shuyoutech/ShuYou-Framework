package com.shuyoutech.common.mongodb.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author YangChao
 * @date 2025-07-14 13:45
 **/
@Data
public class TreeVo implements Serializable {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "根ID")
    private Long rootId;

    @Schema(description = "父级ID")
    private Long parentId;

    @Schema(description = "树结构编码,用于快速查找,用-分割")
    private String treePath;

    @Schema(description = "树形层级")
    private Integer treeLevel;

}
