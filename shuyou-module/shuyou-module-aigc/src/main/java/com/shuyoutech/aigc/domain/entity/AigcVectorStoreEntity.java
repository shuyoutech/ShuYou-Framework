package com.shuyoutech.aigc.domain.entity;

import com.shuyoutech.aigc.domain.vo.AigcVectorStoreVo;
import com.shuyoutech.aigc.enums.VectorStoreTypeEnum;
import com.shuyoutech.common.mongodb.model.BaseEntity;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author YangChao
 * @date 2025-05-12 14:23:37
 **/
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = AigcVectorStoreVo.class)
@Document(collection = "aigc_vector_store")
@Schema(description = "向量存储数据库表类")
public class AigcVectorStoreEntity extends BaseEntity<AigcVectorStoreEntity> {

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
