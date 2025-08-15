package com.shuyoutech.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通用状态枚举
 *
 * @author YangChao
 * @date 2025-07-05 20:18
 **/
@Getter
@AllArgsConstructor
public enum StatusEnum implements BaseEnum<String, String> {

    DISABLE("0", "禁用"),

    ENABLE("1", "正常");

    private final String value;
    private final String label;

}
