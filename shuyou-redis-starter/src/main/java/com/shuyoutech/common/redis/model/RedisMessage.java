package com.shuyoutech.common.redis.model;

import com.shuyoutech.common.core.util.MessageSourceUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author YangChao
 * @date 2025-08-15 15:18
 **/
@Data
public class RedisMessage implements Serializable {

    @Schema(description = "缓存名称")
    private String cacheName;

    @Schema(description = "类型 1-更新,2-删除")
    private String type;

    @Schema(description = "缓存key")
    private String key;

    @Schema(description = "缓存value")
    private String value;

    @Schema(description = "源主机标识，用来避免重复操作")
    private String msgSource;

    public static RedisMessage of(String cacheName, String type, String key, String value) {
        RedisMessage massage = new RedisMessage();
        massage.cacheName = cacheName;
        massage.type = type;
        massage.key = key;
        massage.value = value;
        massage.msgSource = MessageSourceUtils.getMsgSource();
        return massage;
    }

}
