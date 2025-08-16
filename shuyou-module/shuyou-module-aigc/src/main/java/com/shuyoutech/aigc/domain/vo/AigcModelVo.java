package com.shuyoutech.aigc.domain.vo;

import com.shuyoutech.aigc.enums.AiChargeTypeEnum;
import com.shuyoutech.aigc.enums.AiModalityTypeEnum;
import com.shuyoutech.aigc.enums.AiProviderTypeEnum;
import com.shuyoutech.common.core.enums.StatusEnum;
import com.shuyoutech.common.mongodb.model.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author YangChao
 * @date 2025-05-10 09:57:41
 **/
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "模型VO类")
public class AigcModelVo extends BaseVo {

    /**
     * 枚举 {@link StatusEnum}
     */
    @Schema(description = "状态")
    private String status;

    @Schema(description = "状态名称")
    private String statusName;

    /**
     * 枚举 {@link AiProviderTypeEnum}
     */
    @Schema(description = "供应商")
    private String provider;

    @Schema(description = "供应商名称")
    private String providerName;

    @Schema(description = "供应商图标")
    private String providerIcon;

    /**
     * 枚举 {@link ModelTypeEnum}
     */
    @Schema(description = "模型类型")
    private List<String> modelTypes;

    @Schema(description = "模型类型名称")
    private List<String> modelTypeNames;

    @Schema(description = "模型名称")
    private String modelName;

    @Schema(description = "模型别名")
    private String modelAlias;

    /**
     * 枚举 {@link AiModalityTypeEnum}
     */
    @Schema(description = "输入支持")
    private List<String> inputs;

    /**
     * 枚举 {@link AiModalityTypeEnum}
     */
    @Schema(description = "输出支持")
    private List<String> outputs;

    @Schema(description = "模型描述")
    private String modelDesc;

    @Schema(description = "代理地址")
    private String baseUrl;

    @Schema(description = "API 密钥")
    private String apiKey;

    @Schema(description = "模型标签")
    private List<String> modelTags;

    @Schema(description = "是否热门模型")
    private Boolean beenHot = false;

    @Schema(description = "是否支持互联网搜索")
    private Boolean enableSearch = false;

    @Schema(description = "是否支持思考模式")
    private Boolean enableThinking = false;

    /**
     * 枚举 {@link AiChargeTypeEnum}
     * 按量计费、按次计费、免费
     */
    @Schema(title = "计费类型")
    private String chargeType;

    @Schema(title = "计费类型名称")
    private String chargeTypeName;

    @Schema(title = "价格集合")
    private List<AigcModelPriceVo> prices;

}
