package com.shuyoutech.aigc.domain.vo;

import com.shuyoutech.aigc.domain.entity.AigcModelEntity;
import com.shuyoutech.common.mongodb.model.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author YangChao
 * @date 2025-05-17 09:04:22
 **/
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "应用显示类")
public class AigcAppVo extends BaseVo {

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "应用类型")
    private String appType;

    @Schema(description = "应用类型名称")
    private String appTypeName;

    @Schema(description = "应用名称")
    private String appName;

    @Schema(description = "应用图标")
    private String appIcon;

    @Schema(description = "应用描述")
    private String appDesc;

    @Schema(description = "对话模型ID")
    private String chatModelId;

    @Schema(description = "对话模型")
    private AigcModelEntity chatModel;

    @Schema(description = "知识库ID")
    private String knowledgeId;

    @Schema(description = "知识库")
    private AigcKnowledgeVo knowledge;

    @Schema(description = "提示词")
    private String promptText;

}
