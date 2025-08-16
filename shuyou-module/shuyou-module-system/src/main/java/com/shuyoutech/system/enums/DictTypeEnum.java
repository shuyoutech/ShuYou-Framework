package com.shuyoutech.system.enums;

import com.shuyoutech.common.core.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author YangChao
 * @date 2025-07-07 08:58
 **/
@Getter
@AllArgsConstructor
public enum DictTypeEnum implements BaseEnum<String, String> {

    STATUS_TYPE("status_type", "状态类型"),

    WHETHER_TYPE("whether_type", "是否类型"),

    USER_TYPE("user_type", "用户类型"),

    ORG_TYPE("org_type", "机构类型"),

    SEX_TYPE("sex_type", "性别类型"),

    MODEL_TYPE("model_type", "模型类型"),

    PROVIDER_TYPE("provider_type", "供应商类型"),

    VECTOR_STORE_TYPE("vector_store_type", "向量数据库类型"),

    AI_APP_TYPE("ai_app_type", "应用类型"),

    AI_SOURCE_TYPE("ai_source_type", "来源类型"),

    AI_CHARGE_TYPE("ai_charge_type", "计费类型"),

    AI_FEE_RULE("ai_fee_rule", "费用规则类型"),

    AI_TOKEN_PRICE_UNIT("ai_token_price_unit", "token价格单位"),

    CURRENCY_UNIT_TYPE("currency_unit_type", "货币单位类型"),

    ;

    private final String value;
    private final String label;

}
