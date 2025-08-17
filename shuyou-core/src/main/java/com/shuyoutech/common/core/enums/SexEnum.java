package com.shuyoutech.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author YangChao
 * @date 2025-07-07 08:58
 **/
@Getter
@AllArgsConstructor
public enum SexEnum implements BaseEnum<String, String> {

    MALE("male", "男"),

    FEMALE("female", "女"),

    UNKNOWN("unknown", "未知");

    private final String value;
    private final String label;

}
