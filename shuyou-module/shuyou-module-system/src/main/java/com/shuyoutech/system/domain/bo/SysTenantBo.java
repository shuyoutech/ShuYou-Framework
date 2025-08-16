package com.shuyoutech.system.domain.bo;

import com.shuyoutech.common.core.enums.StatusEnum;
import com.shuyoutech.common.core.model.StatusGroup;
import com.shuyoutech.common.core.model.UpdateGroup;
import com.shuyoutech.system.domain.entity.SysTenantEntity;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author YangChao
 * @date 2025-07-07 09:01:15
 **/
@Data
@AutoMapper(target = SysTenantEntity.class)
@Schema(description = "租户类")
public class SysTenantBo implements Serializable {

    @NotBlank(message = "id不能为空", groups = {UpdateGroup.class, StatusGroup.class})
    @Schema(description = "主键")
    private String id;

    /**
     * 枚举 {@link StatusEnum}
     */
    @NotNull(message = "状态不能为空", groups = {StatusGroup.class})
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
