package com.shuyoutech.system.enums;

import com.shuyoutech.common.core.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 机构类型枚举
 *
 * @author YangChao
 * @date 2025-05-12 12:00
 **/
@Getter
@AllArgsConstructor
public enum OrgTypeEnum implements BaseEnum<String, String> {

    GROUP("group", "集团"),

    COMPANY("company", "公司"),

    SUB_COMPANY("subCompany", "分子公司"),

    DEPT("dept", "部门"),

    ;

    private final String value;
    private final String label;

}
