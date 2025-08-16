package com.shuyoutech.aigc.domain.model;

import com.shuyoutech.aigc.domain.entity.AigcModelEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author YangChao
 * @date 2025-05-18 17:45
 **/
@Data
public class UserModelUsage implements Serializable {

    @Schema(description = "ID")
    private String id;

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "用户名称")
    private String userName;

    @Schema(description = "供应商")
    private String provider;

    @Schema(description = "模型")
    private String modelName;

    @Schema(description = "会话ID")
    private String conversationId;

    @Schema(description = "是否开启多轮对话")
    private Boolean enableMemory;

    @Schema(description = "是否开启深度思考")
    private Boolean enableThinking;

    @Schema(description = "是否开启联网搜索")
    private Boolean enableSearch;

    @Schema(description = "模型功能接口")
    private String modelFunction;

    @Schema(description = "历史消息集合")
    private List<ChatMessage> messages;

    @Schema(description = "用户消息")
    private String userMessage;

    @Schema(description = "AI返回消息")
    private String assistantMessage;

    @Schema(description = "请求时间")
    private Date requestTime;

    @Schema(description = "请求内容")
    private String requestBody;

    @Schema(description = "响应时间")
    private Date responseTime;

    @Schema(description = "返回内容")
    private String responseBody;

    @Schema(description = "对话运行时间")
    private BigDecimal durationSeconds;

    @Schema(description = "输入Token数量")
    private Integer inputTokenCount;

    @Schema(description = "输出Token数量")
    private Integer outputTokenCount;

    @Schema(description = "总共Token数量")
    private Integer totalTokenCount;

    @Schema(description = "总数量")
    private Integer totalCount;

    @Schema(description = "总时长秒")
    private Integer totalDuration;

    @Schema(description = "请求IP")
    private String ip;

    @Schema(description = "模型对象")
    private AigcModelEntity model;

}
