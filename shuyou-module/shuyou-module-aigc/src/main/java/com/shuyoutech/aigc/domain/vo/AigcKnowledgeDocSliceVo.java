package com.shuyoutech.aigc.domain.vo;

import com.shuyoutech.common.mongodb.model.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author YangChao
 * @date 2025-05-14 10:35:30
 **/
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "知识库文档切片显示类")
public class AigcKnowledgeDocSliceVo extends BaseVo {

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
