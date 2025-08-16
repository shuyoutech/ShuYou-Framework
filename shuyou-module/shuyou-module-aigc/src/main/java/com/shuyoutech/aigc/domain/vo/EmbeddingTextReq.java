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
@Schema(description = "Embedding模型请求类")
public class EmbeddingTextReq implements Serializable {

    @Schema(description = "知识库ID")
    private String knowledgeId;

    @Schema(description = "文档名称")
    private String docName;

    @Schema(description = "文档内容")
    private String docContent;

}
