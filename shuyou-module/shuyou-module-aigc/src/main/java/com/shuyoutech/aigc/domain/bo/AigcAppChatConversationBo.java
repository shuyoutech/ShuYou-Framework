package com.shuyoutech.aigc.domain.bo;

import com.shuyoutech.aigc.domain.entity.AigcAppChatConversationEntity;
import com.shuyoutech.aigc.enums.AiSourceTypeEnum;
import com.shuyoutech.common.core.model.StatusGroup;
import com.shuyoutech.common.core.model.UpdateGroup;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author YangChao
 * @date 2025-07-12 14:52:04
 **/
@Data
@AutoMapper(target = AigcAppChatConversationEntity.class)
@Schema(description = "对话窗口类")
public class AigcAppChatConversationBo implements Serializable {

    @NotBlank(message = "id不能为空", groups = {UpdateGroup.class, StatusGroup.class})
    @Schema(description = "主键")
    private String id;

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

    @Schema(description = "开始日期")
    private Date startDate;

    @Schema(description = "结束日期")
    private Date endDate;

}
