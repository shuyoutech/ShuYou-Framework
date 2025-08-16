package com.shuyoutech.aigc.domain.entity;

import com.shuyoutech.aigc.domain.vo.AigcAppChatConversationVo;
import com.shuyoutech.aigc.enums.AiSourceTypeEnum;
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
 * @date 2025-05-17 08:59:21
 **/
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = AigcAppChatConversationVo.class)
@Document(collection = "aigc_app_chat_conversation")
@Schema(description = "对话窗口表类")
public class AigcAppChatConversationEntity extends BaseEntity<AigcAppChatConversationEntity> {

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "应用ID")
    private String appId;

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "标题")
    private String title;

    /**
     * 枚举 {@link AiSourceTypeEnum}
     */
    @Schema(description = "来源:online-线上,test-测试")
    private String source;

}
