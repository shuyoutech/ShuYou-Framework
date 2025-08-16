package com.shuyoutech.aigc.domain.vo;

import com.shuyoutech.common.mongodb.model.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author YangChao
 * @date 2025-08-12 22:28:58
 **/
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "供应商VO类")
public class AigcProviderVo extends BaseVo {

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
