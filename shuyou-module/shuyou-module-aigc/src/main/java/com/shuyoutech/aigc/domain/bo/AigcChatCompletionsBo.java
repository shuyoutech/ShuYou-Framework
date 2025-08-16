package com.shuyoutech.aigc.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author YangChao
 * @date 2025-07-19 11:07
 **/
@Data
@Schema(description = "AI对话请求类")
public class AigcChatCompletionsBo implements Serializable {

    @NotBlank(message = "应用ID不能为空")
    @Schema(description = "应用ID")
    private String appId;

    @Schema(description = "会话ID")
    private String conversationId;

    @Schema(description = "消息角色:user,ai,system")
    private String role;

    @Schema(description = "消息对象集合")
    private List<AigcChatCompletionsMessageBo> messages;

}
