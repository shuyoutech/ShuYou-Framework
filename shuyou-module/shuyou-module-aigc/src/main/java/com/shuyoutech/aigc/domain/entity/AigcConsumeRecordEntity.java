package com.shuyoutech.aigc.domain.entity;

import com.shuyoutech.common.mongodb.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.util.Date;

import static org.springframework.data.mongodb.core.mapping.FieldType.DECIMAL128;

/**
 * @author YangChao
 * @date 2025-05-10 09:57:41
 **/
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(collection = "aigc_consume_record")
@Schema(description = "消费记录表类")
public class AigcConsumeRecordEntity extends BaseEntity<AigcConsumeRecordEntity> {

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "用户名称")
    private String userName;

    @Schema(description = "供应商")
    private String provider;

    @Schema(description = "模型")
    private String modelName;

    @Schema(description = "模型功能接口")
    private String modelFunction;

    @Schema(description = "消息类型")
    private String messageType;

    @Schema(description = "消息ID")
    private String messageId;

    @Schema(description = "消费价格")
    @Field(targetType = DECIMAL128)
    private BigDecimal price;

}
