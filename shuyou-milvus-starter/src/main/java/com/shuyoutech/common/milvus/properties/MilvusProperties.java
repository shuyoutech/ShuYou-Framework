package com.shuyoutech.common.milvus.properties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author YangChao
 * @date 2025-08-06 17:17
 **/
@Data
@ConfigurationProperties(prefix = "spring.milvus")
public class MilvusProperties {

    @Schema(description = "地址")
    private String uri;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    private String password;

}
