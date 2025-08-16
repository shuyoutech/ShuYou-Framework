package com.shuyoutech.system.domain.entity;

import com.shuyoutech.common.core.enums.StatusEnum;
import com.shuyoutech.common.mongodb.model.BaseEntity;
import com.shuyoutech.system.domain.vo.SysPostVo;
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
@AutoMapper(target = SysPostVo.class)
@Schema(description = "岗位表")
@Document(collection = "sys_post")
public class SysPostEntity extends BaseEntity<SysPostEntity> {

    /**
     * 枚举 {@link StatusEnum}
     */
    @Schema(description = "状态:0-停用,1-正常")
    private String status;

    @Schema(description = "岗位编码")
    private String postCode;

    @Schema(description = "岗位名称")
    private String postName;

    @Schema(description = "岗位排序")
    private Integer postSort;

    @Schema(description = "岗位描述")
    private String postDesc;

}
