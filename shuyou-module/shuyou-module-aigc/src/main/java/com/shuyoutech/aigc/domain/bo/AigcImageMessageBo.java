package com.shuyoutech.aigc.domain.bo;

import com.shuyoutech.aigc.domain.entity.AigcImageMessageEntity;
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
 * @date 2025-07-27 23:01:14
 **/
@Data
@AutoMapper(target = AigcImageMessageEntity.class)
@Schema(description = "绘画消息类")
public class AigcImageMessageBo implements Serializable {

    @NotBlank(message = "id不能为空", groups = {UpdateGroup.class, StatusGroup.class})
    @Schema(description = "主键")
    private String id;

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "用户名称")
    private String userName;

    @Schema(description = "请求时间")
    private Date requestTime;

    @Schema(description = "请求内容")
    private String requestBody;

    @Schema(description = "响应时间")
    private Date responseTime;

    @Schema(description = "返回内容")
    private String responseBody;

    @Schema(description = "请求IP")
    private String ip;

    @Schema(description = "请求地区")
    private String ipRegion;

}
