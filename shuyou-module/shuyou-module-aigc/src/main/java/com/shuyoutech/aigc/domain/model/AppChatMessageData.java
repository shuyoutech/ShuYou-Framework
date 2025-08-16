package com.shuyoutech.aigc.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author YangChao
 * @date 2025-05-18 17:45
 **/
@Data
@Accessors(chain = true)
public class AppChatMessageData implements Serializable {

    @Schema(description = "应用ID")
    private String appId;

    @Schema(description = "会话ID")
    private String conversationId;

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "来源:online-线上,test-测试")
    private String source;

    @Schema(description = "消息类型:text、image、audio")
    private String messageType;

    @Schema(description = "消息角色:user,assistant")
    private String messageRole;

    @Schema(description = "消息内容")
    private String messageContent;

}
