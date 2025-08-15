package com.shuyoutech.api.model;

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
public class LoginUserRole implements Serializable {

    @Schema(description = "角色ID")
    private String roleId;

    @Schema(description = "角色编码")
    private String roleCode;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "数据范围")
    private String dataScope;

}
