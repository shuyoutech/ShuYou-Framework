package com.shuyoutech.aigc.domain.vo;

import com.shuyoutech.common.mongodb.model.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author YangChao
 * @date 2025-07-27 23:01:14
 **/
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "绘画消息显示类")
public class AigcImageMessageVo extends BaseVo {

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
