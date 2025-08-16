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
 * @date 2025-05-14 09:48:05
 **/
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "知识库文档显示类")
public class AigcKnowledgeDocVo extends BaseVo {

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
