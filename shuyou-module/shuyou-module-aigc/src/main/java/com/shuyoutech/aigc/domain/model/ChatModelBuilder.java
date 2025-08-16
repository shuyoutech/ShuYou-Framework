package com.shuyoutech.aigc.domain.model;

import com.alibaba.fastjson2.JSONObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author YangChao
 * @date 2025-07-13 23:31
 **/
@Data
@Builder
public class ChatModelBuilder implements Serializable {

    @Schema(description = "会话ID")
    private String conversationId;

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "用户名称")
    private String userName;

    @Schema(description = "请求地址")
    private String baseUrl;

    @Schema(description = "apiKey")
    private String apiKey;

    @Schema(description = "模型名称")
    private String modelName;

    @Schema(description = "供应商")
    private String provider;

    /**
     * top_p、temperature、stop、thinking_budget
     */
    @Schema(description = "模型参数对象")
    private JSONObject modelParam;

    @Schema(description = "用户消耗token")
    private UserModelUsage userToken;

}
