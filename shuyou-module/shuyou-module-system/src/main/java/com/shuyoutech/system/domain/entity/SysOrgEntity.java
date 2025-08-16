package com.shuyoutech.system.domain.entity;

import com.shuyoutech.common.core.enums.StatusEnum;
import com.shuyoutech.common.mongodb.model.BaseEntity;
import com.shuyoutech.system.domain.vo.SysOrgVo;
import com.shuyoutech.system.enums.OrgTypeEnum;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author YangChao
 * @date 2025-07-07 19:49
 **/
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysOrgVo.class)
@Schema(description = "机构表")
@Document(collection = "sys_org")
public class SysOrgEntity extends BaseEntity<SysOrgEntity> {

    @Schema(description = "租户ID")
    private String tenantId;

    /**
     * 枚举 {@link StatusEnum}
     */
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
