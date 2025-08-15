package com.shuyoutech.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author YangChao
 * @date 2025-07-07 08:58
 **/
@Getter
@AllArgsConstructor
public enum QuerySymbolEnum implements BaseEnum<String, String> {


    EQ("==", "等于"),

    NE("!=", "不等于"),

    EMPTY("=''", "等于空"),

    NOT_EMPTY("!=''", "不等于空"),

    LIKE("like '%%'", "模糊"),

    NOT_LIKE("is not like '%%'", "不模糊"),

    LEFT_LIKE("like '% '", "左模糊"),

    RIGHT_LIKE("like ' %'", "不等于"),

    GT(">", "大于"),

    LT("<", "小于"),

    GTE(">=", "大于等于"),

    LTE("<=", "小于等于"),

    IN("in", "在.之内"),

    NOT_IN("is not in", "不在.之内"),

    BETWEEN("between", "在.之间"),

    NOT_BETWEEN("is not between", "不在.之间"),

    NULL("is null", "为null"),

    NOT_NULL("is not null", "不为null"),

    EXISTS("exists", "存在"),

    NOT_EXISTS("is not exists", "不存在"),

    QUERY_STRING("query", "es query"),

    MATCH("match", "es match"),

    MATCH_PHRASE("match_phrase", "es match_phrase");

    private final String value;
    private final String label;

}
