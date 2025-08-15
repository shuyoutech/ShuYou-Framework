package com.shuyoutech.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author YangChao
 * @date 2025-07-07 08:58
 **/
@Getter
@AllArgsConstructor
public enum QueryLogicEnum implements BaseEnum<String, String> {

    AND( "and", "且"),

    OR("or", "或");

    private final String value;
    private final String label;

}
