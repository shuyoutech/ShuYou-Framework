package com.shuyoutech.aigc.domain.entity;

import com.shuyoutech.aigc.domain.vo.AigcAppChatMessageVo;
import com.shuyoutech.common.mongodb.model.BaseEntity;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @author YangChao
 * @date 2025-05-16 16:16:03
 **/
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = AigcAppChatMessageVo.class)
@Document(collection = "aigc_app_chat_message")
@Schema(description = "对话消息表类")
public class AigcAppChatMessageEntity extends BaseEntity<AigcAppChatMessageEntity> {

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
