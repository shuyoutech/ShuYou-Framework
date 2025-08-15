package com.shuyoutech.common.core.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author YangChao
 * @date 2025-07-05 19:31
 **/
@Data
@Builder
@Schema(description = "参数校验对象")
public class ParamUnique implements Serializable {

    @Schema(description = "ID")
    private String id;

    @Schema(description = "参数编码")
    @NotBlank(message = "参数编码不能为空")
    private String paramCode;

    @Schema(description = "参数值")
    @NotNull(message = "参数值不能为空")
    private Object paramValue;

}
