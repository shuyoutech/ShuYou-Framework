package com.shuyoutech.system.domain.vo;

import com.shuyoutech.common.core.enums.StatusEnum;
import com.shuyoutech.common.mongodb.model.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author YangChao
 * @date 2025-07-07 09:01:15
 **/
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "租户显示类")
public class SysTenantVo extends BaseVo {

    /**
     * 枚举 {@link StatusEnum}
     */
    @Schema(description = "状态")
    private String status;

    @Schema(description = "租户编码")
    private String tenantCode;

    @Schema(description = "租户名称")
    private String tenantName;

    @Schema(description = "过期时间")
    private Date expireTime;

    @Schema(description = "联系人")
    private String contactUserName;

    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "统一社会信用代码")
    private String unifiedSocialCreditCode;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "租户简介")
    private String tenantDesc;

    @Schema(description = "域名")
    private String tenantDomain;

    @Schema(description = "用户数量（-1不限制）")
    private Integer accountCount;

}
