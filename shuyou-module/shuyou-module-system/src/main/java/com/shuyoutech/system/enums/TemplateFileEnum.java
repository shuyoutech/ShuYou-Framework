package com.shuyoutech.system.enums;

import com.shuyoutech.common.core.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 模版文件枚举
 *
 * @author YangChao
 * @date 2025-07-05 20:18
 **/
@Getter
@AllArgsConstructor
public enum TemplateFileEnum implements BaseEnum<String, String> {

    USER("user", "用户模板.xlsx"),

    ORG("org", "机构模板.xlsx");

    private final String value;
    private final String label;

}
