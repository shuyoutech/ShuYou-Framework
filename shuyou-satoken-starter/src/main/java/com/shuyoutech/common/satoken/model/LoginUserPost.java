package com.shuyoutech.common.satoken.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author YangChao
 * @date 2025-07-08 16:06
 **/
@Data
@Builder
public class LoginUserPost implements Serializable {

    @Schema(description = "岗位ID")
    private String postId;

    @Schema(description = "岗位编码")
    private String postCode;

    @Schema(description = "岗位名称")
    private String postName;

}
