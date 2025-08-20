package com.shuyoutech.common.redis.config.properties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author YangChao
 * @date 2025-08-06 17:17
 **/
@Data
@ConfigurationProperties(prefix = "shuyoutech.cache")
public class CacheProperties {

    @Schema(description = "缓存名称")
    private String name = "shuyoutech";

    @Schema(description = "是否允许控制")
    private Boolean allowNull = true;

    @Schema(description = "Caffeine初始化大小")
    private Integer caffeineInit = 1000;

    @Schema(description = "Caffeine最大值个数")
    private Integer caffeineMax = 10000;

    @Schema(description = "Caffeine过期时间10分-秒单位")
    private Integer caffeineExpire = 600;

    @Schema(description = "Redis过期时间 默认1小时")
    private Integer redisExpire = 3600;

}
