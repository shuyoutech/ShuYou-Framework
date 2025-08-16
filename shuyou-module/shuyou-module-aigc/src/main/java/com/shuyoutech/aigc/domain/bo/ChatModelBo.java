package com.shuyoutech.aigc.domain.bo;

import com.alibaba.fastjson2.JSONObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author YangChao
 * @date 2025-07-13 15:42
 **/
@Data
@Schema(description = "AI对话请求类")
public class ChatModelBo implements Serializable {

    @Schema(description = "会话ID")
    private String conversationId;

    @Schema(description = "供应商")
    private String provider;

    @Schema(description = "模型名称")
    private String model;

    /**
     * 对话、翻译、数学、编程
     */
    @Schema(description = "模型功能")
    private String modelFunction;

    @Schema(description = "模型参数对象")
    private JSONObject modelParam;

}
