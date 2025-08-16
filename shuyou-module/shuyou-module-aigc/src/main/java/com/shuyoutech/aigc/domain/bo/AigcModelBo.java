package com.shuyoutech.aigc.domain.bo;

import com.shuyoutech.aigc.domain.entity.AigcModelPrice;
import com.shuyoutech.common.core.enums.StatusEnum;
import com.shuyoutech.common.core.model.StatusGroup;
import com.shuyoutech.common.core.model.UpdateGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author YangChao
 * @date 2025-07-11 11:18:54
 **/
@Data
@Schema(description = "模型类")
public class AigcModelBo implements Serializable {

    @NotBlank(message = "id不能为空", groups = {UpdateGroup.class, StatusGroup.class})
    @Schema(description = "主键")
    private String id;

    /**
     * 枚举 {@link StatusEnum}
     */
    @NotNull(message = "状态不能为空", groups = {StatusGroup.class})
    @Schema(description = "状态")
    private String status;

    /**
     * 枚举 {@link AiProviderTypeEnum}
     */
    @Schema(description = "供应商")
    private String provider;

    @Schema(description = "模型类型")
    private String modelType;

    /**
     * 枚举 {@link ModelTypeEnum}
     */
    @Schema(description = "模型类型集合")
    private List<String> modelTypes;

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
    private String modelTag;

    @Schema(description = "模型标签")
    private List<String> modelTags;

    @Schema(description = "是否热门模型")
    private Boolean beenHot;

    @Schema(description = "是否支持互联网搜索")
    private Boolean enableSearch;

    @Schema(description = "是否支持思考模式")
    private Boolean enableThinking;

    /**
     * 枚举 {@link AiChargeTypeEnum}
     * 按量计费、按次计费、免费
     */
    @Schema(title = "计费类型")
    private String chargeType;

    @Schema(title = "价格集合")
    private List<AigcModelPrice> prices;

}
