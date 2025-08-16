package com.shuyoutech.aigc.domain.bo;

import com.shuyoutech.aigc.domain.entity.AigcAppChatMessageEntity;
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
 * @date 2025-07-12 15:44:07
 **/
@Data
@AutoMapper(target = AigcAppChatMessageEntity.class)
@Schema(description = "对话消息类")
public class AigcAppChatMessageBo implements Serializable {

    @NotBlank(message = "id不能为空", groups = {UpdateGroup.class, StatusGroup.class})
    @Schema(description = "主键")
    private String id;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "应用ID")
    private String appId;

    @Schema(description = "会话ID")
    private String conversationId;

    @Schema(description = "消息类型:text、image、audio")
    private String messageType;

    @Schema(description = "消息角色:user,assistant")
    private String messageRole;

    @Schema(description = "消息内容")
    private String messageContent;

    @Schema(description = "对话运行时间")
    private Long durationSeconds;

    @Schema(description = "输入Token数量")
    private Integer inputTokenCount;

    @Schema(description = "输出Token数量")
    private Integer outputTokenCount;

    @Schema(description = "总共Token数量")
    private Integer totalTokenCount;

}
