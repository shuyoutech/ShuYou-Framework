package com.shuyoutech.aigc.domain.bo;

import com.shuyoutech.aigc.domain.entity.AigcKnowledgeDocSliceEntity;
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
 * @date 2025-07-11 19:38:09
 **/
@Data
@AutoMapper(target = AigcKnowledgeDocSliceEntity.class)
@Schema(description = "知识库文档切片类")
public class AigcKnowledgeDocSliceBo implements Serializable {

    @NotBlank(message = "id不能为空", groups = {UpdateGroup.class, StatusGroup.class})
    @Schema(description = "主键")
    private String id;

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
