package com.shuyoutech.system.domain.entity;

import com.shuyoutech.common.core.enums.StatusEnum;
import com.shuyoutech.common.mongodb.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @author YangChao
 * @date 2025-07-07 19:58
 **/
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Schema(description = "租户表")
@Document(collection = "sys_tenant")
public class SysTenantEntity extends BaseEntity<SysTenantEntity> {

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
