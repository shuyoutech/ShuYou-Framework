package com.shuyoutech.aigc.domain.entity;

import com.shuyoutech.aigc.domain.vo.AigcModelPriceVo;
import com.shuyoutech.aigc.enums.AiModelFeeRuleEnum;
import com.shuyoutech.aigc.enums.AiTokenPriceUnitEnum;
import com.shuyoutech.aigc.enums.CurrencyUnitTypeEnum;
import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMappers;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.math.BigDecimal;

import static org.springframework.data.mongodb.core.mapping.FieldType.DECIMAL128;

/**
 * @author YangChao
 * @date 2025-07-14 21:54
 **/
@Data
@AutoMappers({@AutoMapper(target = AigcModelPriceVo.class)})
public class AigcModelPrice implements Serializable {

    /**
     * 枚举 {@link AiModelFeeRuleEnum}
     */
    @Schema(description = "收费规则")
    private String feeRule;

    @Schema(description = "提示输入token价格")
    @Field(targetType = DECIMAL128)
    private BigDecimal tokenPrice;

    /**
     * 枚举 {@link AiTokenPriceUnitEnum}
     * 千Token、百万Token
     */
    @Schema(description = "token价格单位")
    private String tokenPriceUnit;

    /**
     * 枚举 {@link CurrencyUnitTypeEnum}
     * 人民币、美元
     */
    @Schema(description = "token货币单位")
    private String currencyUnit;

}
