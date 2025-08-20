package com.shuyoutech.common.redis.message;

import com.shuyoutech.common.redis.enums.CacheMsgTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author YangChao
 * @date 2025-08-06 17:23
 **/
@Data
public class CacheMassage implements Serializable {

    @Schema(description = "缓存名称")
    private String cacheName;

    /**
     * 枚举 {@link CacheMsgTypeEnum}
     */
    @Schema(description = "类型")
    private String type;

    @Schema(description = "缓存key")
    private Object key;

    @Schema(description = "缓存value")
    private Object value;

    @Schema(description = "源主机标识，用来避免重复操作")
    private String msgSource;

}
