package com.shuyoutech.common.milvus.config.properties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author YangChao
 * @date 2025-08-06 17:17
 **/
@Data
@ConfigurationProperties(prefix = "spring.data.milvus")
public class MilvusProperties {

    @Schema(description = "地址", defaultValue = "http://localhost:19530")
    private String uri;

    @Schema(description = "用户名", defaultValue = "root")
    private String username;

    @Schema(description = "密码", defaultValue = "Milvus")
    private String password;

    @Schema(description = "数据库", defaultValue = "default")
    private String database;

}
