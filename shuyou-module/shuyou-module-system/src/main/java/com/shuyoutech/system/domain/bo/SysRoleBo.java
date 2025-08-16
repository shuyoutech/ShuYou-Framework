package com.shuyoutech.system.domain.bo;

import com.shuyoutech.common.core.enums.StatusEnum;
import com.shuyoutech.common.core.model.SaveGroup;
import com.shuyoutech.common.core.model.StatusGroup;
import com.shuyoutech.common.core.model.UpdateGroup;
import com.shuyoutech.system.domain.entity.SysRoleEntity;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * @author YangChao
 * @date 2025-07-07 20:05
 **/
@Data
@AutoMapper(target = SysRoleEntity.class)
@Schema(description = "角色信息业务对象类")
public class SysRoleBo implements Serializable {

    @NotBlank(message = "id不能为空", groups = {UpdateGroup.class, StatusGroup.class})
    @Schema(description = "主键")
    private String id;

    @Schema(description = "租户ID")
    private String tenantId;

    /**
     * 枚举 {@link StatusEnum}
     */
    @NotNull(message = "状态不能为空", groups = {StatusGroup.class})
    @Schema(description = "状态")
    private String status;

    @NotBlank(message = "角色编码不能为空", groups = {SaveGroup.class, UpdateGroup.class})
    @Schema(description = "角色编码")
    private String roleCode;

    @NotBlank(message = "角色名称不能为空", groups = {SaveGroup.class, UpdateGroup.class})
    @Schema(description = "角色名称")
    private String roleName;

    @NotNull(message = "角色排序不能为空", groups = {SaveGroup.class, UpdateGroup.class})
    @Schema(description = "角色排序")
    private Integer roleSort;

    @Schema(description = "角色描述")
    private String roleDes;

    @Schema(description = "数据范围")
    private String dataScope;

    @Schema(description = "菜单组数据权限")
    private Set<String> menuIds;

    @Schema(description = "机构组数据权限")
    private Set<String> orgIds;

}
