package com.shuyoutech.aigc.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author YangChao
 * @date 2025-07-19 11:07
 **/
@Data
@Schema(description = "AI对话请求消息体类")
public class AigcChatCompletionsMessageBo implements Serializable {

    @Schema(description = "消息类型:text、image、audio")
    private String type;

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "图片地址")
    private String imageUrl;

    @Schema(description = "音频地址")
    private String audioUrl;

}
