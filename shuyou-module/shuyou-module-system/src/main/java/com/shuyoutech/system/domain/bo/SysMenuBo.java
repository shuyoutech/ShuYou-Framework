package com.shuyoutech.system.domain.bo;

import com.shuyoutech.common.core.enums.StatusEnum;
import com.shuyoutech.common.core.model.StatusGroup;
import com.shuyoutech.common.core.model.UpdateGroup;
import com.shuyoutech.system.domain.entity.SysMenuEntity;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * @author YangChao
 * @date 2025-07-07 10:44:24
 **/
@Data
@AutoMapper(target = SysMenuEntity.class)
@Schema(description = "菜单类")
public class SysMenuBo implements Serializable {

    @NotBlank(message = "id不能为空", groups = {UpdateGroup.class, StatusGroup.class})
    @Schema(description = "主键")
    private String id;

    /**
     * 枚举 {@link StatusEnum}
     */
    @NotNull(message = "状态不能为空", groups = {StatusGroup.class})
    @Schema(description = "状态")
    private String status;

    @Schema(description = "父级ID")
    private String parentId;

    @Schema(description = "树结构编码,用于快速查找,用-分割")
    private String treePath;

    @Schema(description = "树形层级")
    private Integer treeLevel;

    @Schema(description = "菜单类型:1-目录,2-菜单,3-按钮,4-外链")
    private String menuType;

    @Schema(description = "菜单名称")
    private String menuName;

    @Schema(description = "菜单路径")
    private String menuPath;

    @Schema(description = "菜单排序")
    private Integer menuSort;

    @Schema(description = "菜单描述")
    private String menuDesc;

    @Schema(description = "权限标识")
    private String perms;

    @Schema(description = "菜单图标")
    private String icon;

    @Schema(description = "组件路径")
    private String component;

    @Schema(description = "是否外链")
    private Boolean beenExt;

    @Schema(description = "是否缓存")
    private Boolean beenKeepalive;

    @Schema(description = "是否显示")
    private Boolean beenVisible;

}
