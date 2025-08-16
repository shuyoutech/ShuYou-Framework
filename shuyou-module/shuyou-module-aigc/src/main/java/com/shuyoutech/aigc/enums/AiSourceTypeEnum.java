package com.shuyoutech.aigc.enums;

import com.shuyoutech.common.core.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 来源类型枚举
 *
 * @author YangChao
 * @date 2025-05-12 12:00
 **/
@Getter
@AllArgsConstructor
public enum AiSourceTypeEnum implements BaseEnum<String, String> {

    ONLINE("online", "在线使用"),

    TEST("test", "在线调试"),

    API("api", "API 调用"),

    ;

    private final String value;
    private final String label;

}
