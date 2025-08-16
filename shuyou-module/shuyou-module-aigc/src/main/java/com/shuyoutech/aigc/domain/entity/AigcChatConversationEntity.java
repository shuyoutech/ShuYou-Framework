package com.shuyoutech.aigc.domain.entity;

import com.shuyoutech.aigc.domain.vo.AigcChatConversationVo;
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
 * @date 2025-07-20 17:08:25
 **/
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = AigcChatConversationVo.class)
@Document(collection = "aigc_chat_conversation")
@Schema(description = "对话窗口表类")
public class AigcChatConversationEntity extends BaseEntity<AigcChatConversationEntity> {

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "标题")
    private String title;

}
