package com.shuyoutech.aigc.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author YangChao
 * @date 2025-07-14 21:54
 **/
@Data
@Builder
public class TokenUsage implements Serializable {

    @Schema(description = "输入Token数量")
    private Integer inputTokenCount;

    @Schema(description = "输出Token数量")
    private Integer outputTokenCount;

    @Schema(description = "总共Token数量")
    private Integer totalTokenCount;

}
