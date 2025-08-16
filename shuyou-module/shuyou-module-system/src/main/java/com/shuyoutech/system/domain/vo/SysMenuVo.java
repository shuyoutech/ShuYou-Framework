package com.shuyoutech.system.domain.vo;

import com.shuyoutech.common.core.enums.StatusEnum;
import com.shuyoutech.common.mongodb.model.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author YangChao
 * @date 2025-05-07 14:00
 **/
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "菜单详情类")
public class SysMenuVo extends BaseVo {

    /**
     * 枚举 {@link StatusEnum}
     */
    @Schema(description = "状态:0-停用,1-正常")
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
