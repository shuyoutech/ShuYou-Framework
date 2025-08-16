package com.shuyoutech.system.enums;

import com.shuyoutech.common.core.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据权限类型枚举
 *
 * @author YangChao
 * @date 2025-05-12 12:00
 **/
@Getter
@AllArgsConstructor
public enum DataScopeTypeEnum implements BaseEnum<String, String> {

    ALL("1", "全部数据权限"),

    CUSTOM("2", "自定数据权限"),

    DEPT("3", "部门数据权限"),

    DEPT_AND_CHILD("4", "部门及以下数据权限"),

    SELF("5", "仅本人数据权限"),

    DEPT_AND_CHILD_OR_SELF("6", "部门及以下或本人数据权限");

    private final String value;
    private final String label;

}
