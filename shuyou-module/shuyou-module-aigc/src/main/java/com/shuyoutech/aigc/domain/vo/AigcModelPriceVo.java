package com.shuyoutech.aigc.domain.vo;

import com.shuyoutech.aigc.enums.AiModelFeeRuleEnum;
import com.shuyoutech.aigc.enums.AiTokenPriceUnitEnum;
import com.shuyoutech.aigc.enums.CurrencyUnitTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author YangChao
 * @date 2025-07-14 21:54
 **/
@Data
public class AigcModelPriceVo implements Serializable {

    /**
     * 枚举 {@link AiModelFeeRuleEnum}
     */
    @Schema(description = "收费规则")
    private String feeRule;

    @Schema(description = "收费规则名称")
    private String feeRuleName;

    @Schema(description = "提示输入token价格")
    private BigDecimal tokenPrice;

    /**
     * 枚举 {@link AiTokenPriceUnitEnum}
     * 千Token、百万Token
     */
    @Schema(description = "token价格单位")
    private String tokenPriceUnit;

    @Schema(description = "token价格单位")
    private String tokenPriceUnitName;

    /**
     * 枚举 {@link CurrencyUnitTypeEnum}
     * 人民币、美元
     */
    @Schema(description = "token货币单位")
    private String currencyUnit;

    @Schema(description = "token货币单位名称")
    private String currencyUnitName;


}
