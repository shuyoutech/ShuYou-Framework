package com.shuyoutech.common.mongodb.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author YangChao
 * @date 2025-08-14 13:45
 **/
@Data
public class TreeEntity<T extends TreeEntity<T>> {

    @Id
    @Field(value = "_id")
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
