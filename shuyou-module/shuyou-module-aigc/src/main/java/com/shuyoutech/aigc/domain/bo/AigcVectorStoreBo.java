package com.shuyoutech.aigc.domain.bo;

import com.shuyoutech.aigc.domain.entity.AigcVectorStoreEntity;
import com.shuyoutech.aigc.enums.VectorStoreTypeEnum;
import com.shuyoutech.common.core.model.StatusGroup;
import com.shuyoutech.common.core.model.UpdateGroup;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * @author YangChao
 * @date 2025-07-11 13:44:53
 **/
@Data
@AutoMapper(target = AigcVectorStoreEntity.class)
@Schema(description = "向量数据库类")
public class AigcVectorStoreBo implements Serializable {

    @NotBlank(message = "id不能为空", groups = {UpdateGroup.class, StatusGroup.class})
    @Schema(description = "主键")
    private String id;

    /**
     * 枚举 {@link VectorStoreTypeEnum}
     */
    @Schema(description = "向量数据库类型")
    private String type;

    @Schema(description = "数据库别名")
    private String name;

    @Schema(description = "数据库地址")
    private String host;

    @Schema(description = "数据库端口")
    private Integer port;

    @Schema(description = "数据库用户名")
    private String username;

    @Schema(description = "数据库密码")
    private String password;

    @Schema(description = "数据库名")
    private String databaseName;

    @Schema(description = "表名称")
    private String tableName;

    @Schema(description = "向量纬度")
    private Integer dimension;

}
