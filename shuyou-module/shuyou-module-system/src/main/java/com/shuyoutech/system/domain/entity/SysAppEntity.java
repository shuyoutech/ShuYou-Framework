package com.shuyoutech.system.domain.entity;

import com.shuyoutech.common.mongodb.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author YangChao
 * @date 2025-07-07 00:04
 **/
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Schema(description = "应用表")
@Document(collection = "sys_app")
public class SysAppEntity extends BaseEntity<SysAppEntity> {

    @Schema(description = "应用标识")
    private String appKey;

    @Schema(description = "应用秘钥")
    private String appSecret;

    @Schema(description = "应用名称")
    private String appName;

    @Schema(description = "私钥")
    private String privateKey;

    @Schema(description = "公钥")
    private String publicKey;

}
