package com.shuyoutech.aigc.domain.vo;

import com.shuyoutech.aigc.enums.AiSourceTypeEnum;
import com.shuyoutech.common.mongodb.model.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author YangChao
 * @date 2025-05-17 08:59:21
 **/
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "对话窗口显示类")
public class AigcAppChatConversationVo extends BaseVo {

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "应用ID")
    private String appId;

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "用户名称")
    private String userName;

    @Schema(description = "标题")
    private String title;

    /**
     * 枚举 {@link AiSourceTypeEnum}
     */
    @Schema(description = "来源")
    private String source;

    @Schema(description = "来源名称")
    private String sourceName;

}
