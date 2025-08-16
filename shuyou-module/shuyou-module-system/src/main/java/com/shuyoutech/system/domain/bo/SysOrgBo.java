package com.shuyoutech.system.domain.bo;

import com.shuyoutech.common.core.enums.StatusEnum;
import com.shuyoutech.common.core.model.StatusGroup;
import com.shuyoutech.common.core.model.UpdateGroup;
import com.shuyoutech.system.domain.entity.SysOrgEntity;
import com.shuyoutech.system.enums.OrgTypeEnum;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * @author YangChao
 * @date 2025-07-07 09:17:49
 **/
@Data
@AutoMapper(target = SysOrgEntity.class)
@Schema(description = "机构类")
public class SysOrgBo implements Serializable {

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

    @Schema(description = "父级ID")
    private String parentId;

    @Schema(description = "树结构编码,用于快速查找,用-分割")
    private String treePath;

    @Schema(description = "树形层级")
    private Integer treeLevel;

    /**
     * 枚举 {@link OrgTypeEnum}
     */
    @Schema(description = "机构类型")
    private String orgType;

    @Schema(description = "机构名称")
    private String orgName;

    @Schema(description = "机构排序")
    private Integer orgSort;

    @Schema(description = "负责人ID")
    private String directorId;

}
