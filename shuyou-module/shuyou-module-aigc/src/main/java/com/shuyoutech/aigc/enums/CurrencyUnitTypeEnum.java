package com.shuyoutech.aigc.enums;

import com.shuyoutech.common.core.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 货币类型枚举
 *
 * @author YangChao
 * @date 2025-05-12 12:00
 **/
@Getter
@AllArgsConstructor
public enum CurrencyUnitTypeEnum implements BaseEnum<String, String> {

    CNY("CNY", "人民币"),

    USD("USD", "美元");

    private final String value;
    private final String label;

}
