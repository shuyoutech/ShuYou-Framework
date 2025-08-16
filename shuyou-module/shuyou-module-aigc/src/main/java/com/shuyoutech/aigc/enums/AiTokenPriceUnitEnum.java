package com.shuyoutech.aigc.enums;

import com.shuyoutech.common.core.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * token价格单位枚举
 *
 * @author YangChao
 * @date 2025-05-12 12:00
 **/
@Getter
@AllArgsConstructor
public enum AiTokenPriceUnitEnum implements BaseEnum<String, String> {

    TOKEN_1K("1K", "千Token"),

    TOKEN_1M("1M", "百万Token"),

    COUNT("1", "一个"),

    ;

    private final String value;
    private final String label;

}
