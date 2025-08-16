package com.shuyoutech.aigc.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author YangChao
 * @date 2025-07-14 21:58
 **/
@Data
@Builder
public class ChatMessage implements Serializable {

    @Schema(description = "角色")
    private String role;

    @Schema(description = "内容")
    private Object content;

}
