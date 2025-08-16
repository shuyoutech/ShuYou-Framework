package com.shuyoutech.system.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

/**
 * @author YangChao
 * @date 2025-07-01 16:09
 **/
@Data
@Configuration
@ConfigurationProperties(prefix = "shuyoutech.storage")
public class FileStorageProperties implements Serializable {

    @Schema(description = "上传目录")
    private String uploadDir = "/usr/share/nginx/html/upload";

    @Schema(description = "服务地址")
    private String endpoint;

    @Schema(description = "用户key")
    private String accessKey;

    @Schema(description = "用户私钥key")
    private String secretKey;

    @Schema(description = "存储桶名称")
    private String bucketName;

    @Schema(description = "预览前缀")
    private String previewPrefix = "https://www.shuyoutech.com/upload";

}
