package com.shuyoutech.common.cache.enums;

import com.shuyoutech.common.core.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author YangChao
 * @date 2025-08-06 17:20
 **/
@Getter
@AllArgsConstructor
public enum CacheMsgTypeEnum implements BaseEnum<String, String> {

    UPDATE("update", "更新"),

    DELETE("delete", "删除");

    private final String value;
    private final String label;

}
