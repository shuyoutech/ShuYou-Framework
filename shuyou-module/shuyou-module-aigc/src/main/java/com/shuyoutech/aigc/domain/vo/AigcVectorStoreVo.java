package com.shuyoutech.aigc.domain.vo;

import com.shuyoutech.aigc.enums.VectorStoreTypeEnum;
import com.shuyoutech.common.mongodb.model.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author YangChao
 * @date 2025-05-12 14:23:37
 **/
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "向量数据库配置显示类")
public class AigcVectorStoreVo extends BaseVo {

    /**
     * 枚举 {@link VectorStoreTypeEnum}
     */
    @Schema(description = "向量数据库类型")
    private String type;

    @Schema(description = "向量数据库类型")
    private String typeName;

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
