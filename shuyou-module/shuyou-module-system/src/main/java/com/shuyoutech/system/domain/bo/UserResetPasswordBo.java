package com.shuyoutech.system.domain.bo;

import com.shuyoutech.system.domain.entity.SysUserEntity;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * @author YangChao
 * @date 2025-07-07 09:32:59
 **/
@Data
@AutoMapper(target = SysUserEntity.class)
@Schema(description = "用户重置密码类")
public class UserResetPasswordBo implements Serializable {

    @NotBlank(message = "用户ID不能为空")
    @Schema(description = "用户ID")
    private String userId;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码")
    private String password;

}
