package com.shuyoutech.system.domain.entity;

import com.shuyoutech.common.core.enums.StatusEnum;
import com.shuyoutech.common.mongodb.model.BaseEntity;
import com.shuyoutech.system.domain.vo.SysRoleVo;
import com.shuyoutech.system.enums.DataScopeTypeEnum;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Set;

/**
 * @author YangChao
 * @date 2025-07-07 00:04
 **/
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysRoleVo.class)
@Schema(description = "角色表")
@Document(collection = "sys_role")
public class SysRoleEntity extends BaseEntity<SysRoleEntity> {

    @Schema(description = "租户ID")
    private String tenantId;

    /**
     * 枚举 {@link StatusEnum}
     */
    @Schema(description = "状态")
    private String status;

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

    @Schema(description = "创建者组织ID")
    private String createOrgId;

    @Schema(description = "更新时间")
    private Date updateTime;

    @Schema(description = "更新人ID")
    private String updateUserId;

}
