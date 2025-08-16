package com.shuyoutech.aigc.domain.bo;

import com.shuyoutech.aigc.domain.entity.AigcKnowledgeDocEntity;
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
 * @date 2025-07-11 19:13:02
 **/
@Data
@AutoMapper(target = AigcKnowledgeDocEntity.class)
@Schema(description = "知识库文档类")
public class AigcKnowledgeDocBo implements Serializable {

    @NotBlank(message = "id不能为空", groups = {UpdateGroup.class, StatusGroup.class})
    @Schema(description = "主键")
    private String id;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "知识库ID")
    private String knowledgeId;

    @Schema(description = "文档类型:file-文件,text-文本")
    private String docType;

    @Schema(description = "文档名称")
    private String docName;

    @Schema(description = "文件ID")
    private String docFileId;

    @Schema(description = "文档内容")
    private String docContent;

    @Schema(description = "切片状态")
    private Boolean sliceStatus;

    @Schema(description = "切片数量")
    private Integer sliceNum;

}
