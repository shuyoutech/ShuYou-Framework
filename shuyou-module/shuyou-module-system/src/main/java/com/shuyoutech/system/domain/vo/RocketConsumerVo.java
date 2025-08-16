package com.shuyoutech.system.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author YangChao
 * @date 2025-03-26 21:42
 **/
@Data
public class RocketConsumerVo implements Serializable {

    @Schema(description = "Tag")
    private String tag;

    @Schema(description = "body")
    private String body;

}
