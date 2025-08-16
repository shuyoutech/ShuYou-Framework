package com.shuyoutech.aigc.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author YangChao
 * @date 2025-05-10 09:57:41
 **/
@Data
@Accessors(chain = true)
@Schema(description = "Embedding模型类")
public class EmbeddingText implements Serializable {

    @Schema(description = "写入到vector store的ID")
    private String vectorId;

    @Schema(description = "文档ID")
    private String docId;

    @Schema(description = "知识库ID")
    private String knowledgeId;

    @Schema(description = "Embedding后切片的文本")
    private String text;

}
