package com.shuyoutech.aigc.domain.vo;

import com.shuyoutech.aigc.domain.entity.AigcModelEntity;
import com.shuyoutech.aigc.domain.entity.AigcVectorStoreEntity;
import com.shuyoutech.common.mongodb.model.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author YangChao
 * @date 2025-05-12 10:30:35
 **/
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "知识库显示类")
public class AigcKnowledgeVo extends BaseVo {

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "知识库名称")
    private String knowledgeName;

    @Schema(description = "向量数据库ID")
    private String vectorStoreId;

    @Schema(description = "向量数据库")
    private AigcVectorStoreEntity vectorStore;

    @Schema(description = "向量模型ID")
    private String embeddingModelId;

    @Schema(description = "向量模型")
    private AigcModelEntity embeddingModel;

    @Schema(description = "知识库描述")
    private String knowledgeDesc;

}
