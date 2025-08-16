package com.shuyoutech.aigc.domain.bo;

import com.shuyoutech.aigc.domain.entity.AigcAppEntity;
import com.shuyoutech.common.core.model.StatusGroup;
import com.shuyoutech.common.core.model.UpdateGroup;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * @author YangChao
 * @date 2025-07-12 09:29:09
 **/
@Data
@AutoMapper(target = AigcAppEntity.class)
@Schema(description = "应用类")
public class AigcAppBo implements Serializable {

    @NotBlank(message = "id不能为空", groups = {UpdateGroup.class, StatusGroup.class})
    @Schema(description = "主键")
    private String id;

    /**
     * 枚举 {@link AppTypeEnum}
     */
    @Schema(description = "应用类型")
    private String appType;

    @Schema(description = "应用名称")
    private String appName;

    @Schema(description = "应用图标")
    private String appIcon;

    @Schema(description = "应用描述")
    private String appDesc;

    @Schema(description = "对话模型ID")
    private String chatModelId;

    @Schema(description = "知识库ID")
    private String knowledgeId;

    @Schema(description = "提示词")
    private String promptText;

}
