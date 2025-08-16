package com.shuyoutech.aigc.domain.entity;

import com.shuyoutech.aigc.domain.vo.AigcProviderVo;
import com.shuyoutech.aigc.enums.AiProviderTypeEnum;
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
 * @date 2025-08-12 22:28:58
 **/
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = AigcProviderVo.class)
@Document(collection = "aigc_provider")
@Schema(description = "供应商表类")
public class AigcProviderEntity extends BaseEntity<AigcProviderEntity> {

    /**
     * 枚举 {@link AiProviderTypeEnum}
     */
    @Schema(description = "供应商编码")
    private String providerCode;

    @Schema(description = "供应商名称")
    private String providerName;

    @Schema(description = "供应商图标")
    private String providerIcon;

    @Schema(description = "供应商排序")
    private Integer providerSort;

    @Schema(description = "供应商描述")
    private String providerDesc;

}
