package com.shuyoutech.common.disruptor.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author YangChao
 * @date 2025-03-26 21:42
 **/
@Data
public class DisruptorData implements Serializable {

    @Schema(description = "实现类名")
    private String serviceName;

    @Schema(description = "请求对象")
    private Object data;

}
