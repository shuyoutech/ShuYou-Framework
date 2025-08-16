package com.shuyoutech.aigc.domain.entity;

import com.shuyoutech.aigc.domain.vo.AigcKnowledgeDocSliceVo;
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
 * @date 2025-05-14 10:35:30
 **/
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = AigcKnowledgeDocSliceVo.class)
@Document(collection = "aigc_knowledge_doc_slice")
@Schema(description = "知识库文档切片表类")
public class AigcKnowledgeDocSliceEntity extends BaseEntity<AigcKnowledgeDocSliceEntity> {

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "知识库ID")
    private String knowledgeId;

    @Schema(description = "文档ID")
    private String docId;

    @Schema(description = "文档名称")
    private String docName;

    @Schema(description = "向量库的ID")
    private String vectorId;

    @Schema(description = "切片内容")
    private String content;

}
