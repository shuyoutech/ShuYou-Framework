package com.shuyoutech.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author YangChao
 * @date 2025-07-05 20:16
 **/
@Getter
@AllArgsConstructor
public enum WhetherEnum implements BaseEnum<String, String> {

    YES("true", "是"),

    NO("false", "否");

    private final String value;
    private final String label;

}
