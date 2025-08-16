package com.shuyoutech.aigc.domain.entity;

import com.shuyoutech.aigc.domain.vo.AigcKnowledgeVo;
import com.shuyoutech.common.mongodb.model.BaseEntity;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @author YangChao
 * @date 2025-05-12 10:30:35
 **/
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = AigcKnowledgeVo.class)
@Document(collection = "aigc_knowledge")
@Schema(description = "知识库表类")
public class AigcKnowledgeEntity extends BaseEntity<AigcKnowledgeEntity> {

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
