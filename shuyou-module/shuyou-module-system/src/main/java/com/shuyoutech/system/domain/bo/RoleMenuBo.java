package com.shuyoutech.system.domain.bo;

import com.shuyoutech.system.domain.entity.SysRoleEntity;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author YangChao
 * @date 2025-07-07 20:05
 **/
@Data
@AutoMapper(target = SysRoleEntity.class)
@Schema(description = "角色菜单对象类")
public class RoleMenuBo implements Serializable {

    @NotBlank(message = "角色ID不能为空")
    @Schema(description = "角色ID")
    private String roleId;

    @NotNull(message = "菜单IDS不能为空")
    @Schema(description = "菜单IDS")
    private List<String> menuIds;

}
