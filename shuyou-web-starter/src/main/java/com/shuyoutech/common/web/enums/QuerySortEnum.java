package com.shuyoutech.common.web.enums;

import com.shuyoutech.common.core.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author YangChao
 * @since 2025-07-05 20:36
 **/
@Getter
@AllArgsConstructor
public enum QuerySortEnum implements BaseEnum<String, String> {


    ASC("asc", "升序"),

    DESC("desc", "降序");

    private final String value;
    private final String label;
}
