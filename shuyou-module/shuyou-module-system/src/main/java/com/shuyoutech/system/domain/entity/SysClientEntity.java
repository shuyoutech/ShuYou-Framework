package com.shuyoutech.system.domain.entity;

import com.shuyoutech.common.mongodb.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author YangChao
 * @date 2025-07-07 00:04
 **/
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Schema(description = "授权客户对象表")
@Document(collection = "sys_client")
public class SysClientEntity extends BaseEntity<SysClientEntity> {

    @Schema(description = "客户端ID")
    private String clientId;

    @Schema(description = "客户端Key")
    private String clientKey;

    @Schema(description = "客户端密钥")
    private String clientSecret;

    @Schema(description = "资源列表")
    private String resourceIds;

    @Schema(description = "访问范围")
    private String scope;

    @Schema(description = "认证类型")
    private String authorizedGrantTypes;

    @Schema(description = "重定向地址")
    private String webServerRedirectUri;

    @Schema(description = "角色列表")
    private String authorities;

    @Schema(description = "token有效期")
    private Integer accessTokenValidity;

    @Schema(description = "刷新令牌有效期")
    private Integer refreshTokenValidity;

    @Schema(description = "令牌扩展字段JSON")
    private String additionalInformation;

    @Schema(description = "是否自动放行")
    private String autoApprove;

}
