package com.shuyoutech.aigc.domain.bo;

import com.shuyoutech.aigc.domain.entity.AigcKnowledgeEntity;
import com.shuyoutech.common.core.model.StatusGroup;
import com.shuyoutech.common.core.model.UpdateGroup;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author YangChao
 * @date 2025-07-11 15:01:17
 **/
@Data
@AutoMapper(target = AigcKnowledgeEntity.class)
@Schema(description = "知识库类")
public class AigcKnowledgeBo implements Serializable {

    @NotBlank(message = "id不能为空", groups = {UpdateGroup.class, StatusGroup.class})
    @Schema(description = "主键")
    private String id;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "知识库名称")
    private String knowledgeName;

    @Schema(description = "向量数据库ID")
    private String vectorStoreId;

    @Schema(description = "向量模型ID")
    private String embeddingModelId;

    @Schema(description = "知识库描述")
    private String knowledgeDesc;

}
