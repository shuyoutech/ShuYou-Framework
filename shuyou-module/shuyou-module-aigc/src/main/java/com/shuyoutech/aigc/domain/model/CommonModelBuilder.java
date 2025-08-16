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
public class CommonModelBuilder implements Serializable {

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "用户名称")
    private String userName;

    @Schema(description = "请求地址")
    private String baseUrl;

    @Schema(description = "apiKey")
    private String apiKey;

    @Schema(description = "供应商")
    private String provider;

    @Schema(description = "模型名称")
    private String modelName;

    @Schema(description = "模型参数对象")
    private JSONObject modelParam;

    @Schema(description = "用户模型使用")
    private UserModelUsage userModelUsage;

}
