package com.shuyoutech.aigc.enums;

import com.shuyoutech.common.core.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息角色枚举
 *
 * @author YangChao
 * @date 2025-05-12 12:00
 **/
@Getter
@AllArgsConstructor
public enum MessageRoleEnum implements BaseEnum<String, String> {

    /**
     * user
     */
    USER("user", "用户"),

    /**
     * assistant
     */
    ASSISTANT("assistant", "AI"),

    /**
     * system
     */
    SYSTEM("system", "系统"),

    /**
     * tool
     */
    TOOL("tool", "工具");

    private final String value;
    private final String label;

}
