package com.shuyoutech.system.domain.vo;

import com.shuyoutech.common.core.enums.StatusEnum;
import com.shuyoutech.common.mongodb.model.BaseVo;
import com.shuyoutech.system.enums.DataScopeTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.Set;

/**
 * @author YangChao
 * @date 2025-07-07 20:06
 **/
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "角色详情类")
public class SysRoleVo extends BaseVo {

    @Schema(description = "租户ID")
    private String tenantId;

    /**
     * 枚举 {@link StatusEnum}
     */
    @Schema(description = "状态")
    private String status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "角色编码")
    private String roleCode;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色排序")
    private Integer roleSort;

    @Schema(description = "角色描述")
    private String roleDes;

    /**
     * 枚举 {@link DataScopeTypeEnum}
     */
    @Schema(description = "数据范围")
    private String dataScope;

    @Schema(description = "菜单组数据权限")
    private Set<String> menuIds;

    @Schema(description = "机构组数据权限")
    private Set<String> orgIds;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "创建人ID")
    private String createUserId;

    @Schema(description = "创建人名称")
    private String createUserName;

    @Schema(description = "创建者组织ID")
    private String createOrgId;

    @Schema(description = "创建者组织名称")
    private String createOrgName;

    @Schema(description = "更新时间")
    private Date updateTime;

    @Schema(description = "更新人ID")
    private String updateUserId;

    @Schema(description = "更新人名称")
    private String updateUserName;

}
