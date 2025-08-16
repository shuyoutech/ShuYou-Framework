package com.shuyoutech.system.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * @author YangChao
 * @date 2025-07-05 11:25
 **/
@Data
public class CommonTemplateFileVo implements Serializable {

    @Schema(description = "模版文件类型 1-短名单库")
    @NotBlank(message = "模版文件类型不能为空")
    private String templateType;

}
