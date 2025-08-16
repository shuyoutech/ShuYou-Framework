package com.shuyoutech.system.domain.bo;

import com.shuyoutech.system.domain.entity.SysRoleEntity;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * @author YangChao
 * @date 2025-07-07 20:05
 **/
@Data
@AutoMapper(target = SysRoleEntity.class)
@Schema(description = "用户角色信息业务对象类")
public class RoleUserBo implements Serializable {

    @NotBlank(message = "用户ID不能为空")
    @Schema(description = "用户ID")
    private String userId;

    @NotBlank(message = "角色ID不能为空")
    @Schema(description = "角色ID")
    private String roleId;

}
